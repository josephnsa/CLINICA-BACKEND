package com.clinica.salud.modules.billing.infrastructure.sunat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

/**
 * Cliente HTTP para enviar comprobantes al PSE Nubefact (REST API).
 * Documentación: https://www.nubefact.com/api-doc-boleta-factura/
 */
@Component
@Slf4j
public class NubefactPseClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient http;
    private final ObjectMapper mapper;
    private final String pseUrl;
    private final String pseToken;

    public NubefactPseClient(
            @Value("${sunat.pse.url:https://api.nubefact.com/api/v1}") String pseUrl,
            @Value("${sunat.pse.token:}") String pseToken) {
        this.http     = new OkHttpClient();
        this.mapper   = new ObjectMapper();
        this.pseUrl   = pseUrl;
        this.pseToken = pseToken;
    }

    /**
     * Envía el XML firmado al PSE y devuelve el CDR (respuesta SUNAT) como String.
     * Si el token no está configurado, simula una respuesta aceptada (modo sandbox).
     *
     * @param signedXml  XML firmado
     * @param docType    "01" (factura) o "03" (boleta)
     * @param serie      Serie del comprobante (ej. "B001")
     * @param number     Correlativo del comprobante
     * @return CDR XML o mensaje de respuesta
     */
    public String send(String signedXml, String docType, String serie, int number) {
        if (pseToken == null || pseToken.isBlank()) {
            log.warn("sunat.pse.token no configurado — simulando respuesta ACCEPTED (modo sandbox)");
            return "<cdr>SANDBOX_ACCEPTED</cdr>";
        }
        try {
            String xmlBase64 = Base64.getEncoder().encodeToString(signedXml.getBytes());
            String fileName  = serie + "-" + String.format("%08d", number) + ".xml";

            String body = String.format(
                    "{\"operacion\":\"generar_comprobante\",\"tipo_de_comprobante\":%s," +
                    "\"archivo\":{\"nombre\":\"%s\",\"contenido\":\"%s\"}}",
                    docType, fileName, xmlBase64);

            Request request = new Request.Builder()
                    .url(pseUrl + "/invoice")
                    .addHeader("Authorization", "Token " + pseToken)
                    .post(RequestBody.create(body, JSON))
                    .build();

            try (Response response = http.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (!response.isSuccessful()) {
                    log.error("PSE Nubefact error {}: {}", response.code(), responseBody);
                    throw new RuntimeException("PSE respondió con error " + response.code() + ": " + responseBody);
                }
                JsonNode node = mapper.readTree(responseBody);
                if (node.has("cdr_xml")) {
                    return new String(Base64.getDecoder().decode(node.get("cdr_xml").asText()));
                }
                return responseBody;
            }
        } catch (IOException e) {
            log.error("Error de comunicación con PSE Nubefact: {}", e.getMessage(), e);
            throw new RuntimeException("Error de comunicación con PSE SUNAT", e);
        }
    }
}
