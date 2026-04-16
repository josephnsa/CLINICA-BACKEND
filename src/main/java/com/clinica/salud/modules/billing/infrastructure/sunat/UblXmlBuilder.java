package com.clinica.salud.modules.billing.infrastructure.sunat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Construye el XML UBL 2.1 requerido por SUNAT para facturas y boletas.
 * Spec: https://cpe.sunat.gob.pe/
 */
@Component
@Slf4j
public class UblXmlBuilder {

    private final JdbcTemplate jdbcTemplate;
    private final String ruc;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public UblXmlBuilder(JdbcTemplate jdbcTemplate,
                         @Value("${sunat.ruc:20000000001}") String ruc) {
        this.jdbcTemplate = jdbcTemplate;
        this.ruc = ruc;
    }

    /**
     * Genera el XML UBL 2.1 para la factura/boleta indicada.
     *
     * @param invoiceId UUID de la factura
     * @return String con el XML completo
     */
    public String build(UUID invoiceId) {
        Map<String, Object> inv = jdbcTemplate.queryForMap("""
                SELECT i.serie, i.number, i.subtotal, i.tax, i.total,
                       i.created_at,
                       p.first_name || ' ' || p.last_name AS patient_name,
                       p.doc_type, p.doc_number,
                       se.name AS sede_name
                FROM invoices i
                JOIN patients p ON i.patient_id = p.id
                JOIN sedes se   ON i.sede_id    = se.id
                WHERE i.id = ?
                """, invoiceId);

        List<Map<String, Object>> items = jdbcTemplate.queryForList("""
                SELECT description, quantity, unit_price, total
                FROM invoice_items
                WHERE invoice_id = ?
                ORDER BY id
                """, invoiceId);

        String serie  = String.valueOf(inv.get("serie"));
        String number = String.format("%08d", inv.get("number"));
        // "B" series = boleta (03), "F" series = factura (01)
        String docType = serie.startsWith("B") ? "03" : "01";
        String issueDate = formatDate(inv.get("created_at"));
        BigDecimal subtotal = toBD(inv.get("subtotal"));
        BigDecimal tax      = toBD(inv.get("tax"));
        BigDecimal total    = toBD(inv.get("total"));

        String docTypeLabel = "03".equals(docType) ? "BOLETA DE VENTA ELECTRONICA"
                                                   : "FACTURA ELECTRONICA";

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\"\n");
        sb.append("         xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\"\n");
        sb.append("         xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\">\n");

        // Cabecera
        sb.append("  <cbc:UBLVersionID>2.1</cbc:UBLVersionID>\n");
        sb.append("  <cbc:CustomizationID>2.0</cbc:CustomizationID>\n");
        sb.append("  <cbc:ID>").append(serie).append("-").append(number).append("</cbc:ID>\n");
        sb.append("  <cbc:IssueDate>").append(issueDate).append("</cbc:IssueDate>\n");
        sb.append("  <cbc:InvoiceTypeCode listID=\"0101\">").append(docType).append("</cbc:InvoiceTypeCode>\n");
        sb.append("  <cbc:Note languageLocaleID=\"1000\"><![CDATA[").append(docTypeLabel).append("]]></cbc:Note>\n");
        sb.append("  <cbc:DocumentCurrencyCode>PEN</cbc:DocumentCurrencyCode>\n");

        // Emisor
        sb.append("  <cac:AccountingSupplierParty>\n");
        sb.append("    <cac:Party>\n");
        sb.append("      <cac:PartyIdentification>\n");
        sb.append("        <cbc:ID schemeID=\"6\">").append(ruc).append("</cbc:ID>\n");
        sb.append("      </cac:PartyIdentification>\n");
        sb.append("      <cac:PartyName><cbc:Name><![CDATA[CLINICA YOSELIN]]></cbc:Name></cac:PartyName>\n");
        sb.append("    </cac:Party>\n");
        sb.append("  </cac:AccountingSupplierParty>\n");

        // Receptor
        String receiverDocScheme = "DNI".equalsIgnoreCase(String.valueOf(inv.get("doc_type"))) ? "1" : "6";
        sb.append("  <cac:AccountingCustomerParty>\n");
        sb.append("    <cac:Party>\n");
        sb.append("      <cac:PartyIdentification>\n");
        sb.append("        <cbc:ID schemeID=\"").append(receiverDocScheme).append("\">")
          .append(inv.get("doc_number")).append("</cbc:ID>\n");
        sb.append("      </cac:PartyIdentification>\n");
        sb.append("      <cac:PartyName><cbc:Name><![CDATA[").append(inv.get("patient_name"))
          .append("]]></cbc:Name></cac:PartyName>\n");
        sb.append("    </cac:Party>\n");
        sb.append("  </cac:AccountingCustomerParty>\n");

        // Totales
        sb.append("  <cac:TaxTotal>\n");
        sb.append("    <cbc:TaxAmount currencyID=\"PEN\">").append(tax).append("</cbc:TaxAmount>\n");
        sb.append("    <cac:TaxSubtotal>\n");
        sb.append("      <cbc:TaxableAmount currencyID=\"PEN\">").append(subtotal).append("</cbc:TaxableAmount>\n");
        sb.append("      <cbc:TaxAmount currencyID=\"PEN\">").append(tax).append("</cbc:TaxAmount>\n");
        sb.append("      <cac:TaxCategory>\n");
        sb.append("        <cac:TaxScheme><cbc:ID>1000</cbc:ID><cbc:Name>IGV</cbc:Name></cac:TaxScheme>\n");
        sb.append("      </cac:TaxCategory>\n");
        sb.append("    </cac:TaxSubtotal>\n");
        sb.append("  </cac:TaxTotal>\n");

        sb.append("  <cac:LegalMonetaryTotal>\n");
        sb.append("    <cbc:LineExtensionAmount currencyID=\"PEN\">").append(subtotal).append("</cbc:LineExtensionAmount>\n");
        sb.append("    <cbc:TaxExclusiveAmount currencyID=\"PEN\">").append(subtotal).append("</cbc:TaxExclusiveAmount>\n");
        sb.append("    <cbc:PayableAmount currencyID=\"PEN\">").append(total).append("</cbc:PayableAmount>\n");
        sb.append("  </cac:LegalMonetaryTotal>\n");

        // Líneas
        int lineNum = 1;
        for (Map<String, Object> item : items) {
            BigDecimal qty       = toBD(item.get("quantity"));
            BigDecimal unitPrice = toBD(item.get("unit_price"));
            BigDecimal lineTotal = toBD(item.get("total"));
            BigDecimal unitBase  = unitPrice.divide(BigDecimal.valueOf(1.18), 6, java.math.RoundingMode.HALF_UP);

            sb.append("  <cac:InvoiceLine>\n");
            sb.append("    <cbc:ID>").append(lineNum++).append("</cbc:ID>\n");
            sb.append("    <cbc:InvoicedQuantity unitCode=\"NIU\">").append(qty).append("</cbc:InvoicedQuantity>\n");
            sb.append("    <cbc:LineExtensionAmount currencyID=\"PEN\">")
              .append(lineTotal.divide(BigDecimal.valueOf(1.18), 2, java.math.RoundingMode.HALF_UP))
              .append("</cbc:LineExtensionAmount>\n");
            sb.append("    <cac:Item><cbc:Description><![CDATA[").append(item.get("description"))
              .append("]]></cbc:Description></cac:Item>\n");
            sb.append("    <cac:Price><cbc:PriceAmount currencyID=\"PEN\">").append(unitBase)
              .append("</cbc:PriceAmount></cac:Price>\n");
            sb.append("  </cac:InvoiceLine>\n");
        }

        sb.append("</Invoice>");
        return sb.toString();
    }

    private String formatDate(Object val) {
        if (val instanceof LocalDateTime ldt) return ldt.format(DATE_FMT);
        return LocalDateTime.now().format(DATE_FMT);
    }

    private BigDecimal toBD(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        try { return new BigDecimal(val.toString()); }
        catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }
}
