package com.clinica.salud.modules.billing.infrastructure.sunat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

/**
 * Firma digitalmente el XML UBL con el certificado digital de la empresa.
 * Usa javax.xml.crypto.dsig (incluido en el JDK — sin dependencias extra).
 */
@Component
@Slf4j
public class XmlDigitalSigner {

    private final String keystorePath;
    private final String keystorePassword;

    public XmlDigitalSigner(
            @Value("${sunat.keystore.path:}") String keystorePath,
            @Value("${sunat.keystore.password:}") String keystorePassword) {
        this.keystorePath     = keystorePath;
        this.keystorePassword = keystorePassword;
    }

    /**
     * Firma el XML recibido. Si no hay keystore configurado, devuelve el XML sin firmar
     * (modo desarrollo/sandbox).
     *
     * @param xmlContent XML UBL a firmar
     * @return XML firmado (o el mismo XML si no hay keystore)
     */
    public String sign(String xmlContent) {
        if (keystorePath == null || keystorePath.isBlank()) {
            log.warn("sunat.keystore.path no configurado — devolviendo XML sin firma (modo sandbox)");
            return xmlContent;
        }
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(getClass().getResourceAsStream(keystorePath),
                    keystorePassword.toCharArray());

            String alias = ks.aliases().nextElement();
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, keystorePassword.toCharArray());
            X509Certificate cert  = (X509Certificate) ks.getCertificate(alias);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            Reference ref = fac.newReference("",
                    fac.newDigestMethod(DigestMethod.SHA1, null),
                    List.of(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null, null);

            SignedInfo si = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref));

            KeyInfoFactory kif = fac.getKeyInfoFactory();
            X509Data xd = kif.newX509Data(Collections.singletonList(cert));
            KeyInfo ki  = kif.newKeyInfo(Collections.singletonList(xd));

            XMLSignature signature = fac.newXMLSignature(si, ki);
            DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement());
            signature.sign(dsc);

            Transformer tf = TransformerFactory.newInstance().newTransformer();
            StringWriter sw = new StringWriter();
            tf.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();

        } catch (Exception e) {
            log.error("Error al firmar XML SUNAT: {}", e.getMessage(), e);
            throw new RuntimeException("Error al firmar XML SUNAT", e);
        }
    }
}
