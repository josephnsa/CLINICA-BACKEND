package com.clinica.salud.modules.integration.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ITextPdfService implements PdfService {

    private final JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─── FACTURA ──────────────────────────────────────────────────────────────

    @Override
    public byte[] generateInvoicePdf(UUID invoiceId) {
        Map<String, Object> invoice = jdbcTemplate.queryForMap("""
                SELECT i.id, i.serie, i.number, i.status, i.subtotal, i.tax, i.total,
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            String code = invoice.get("serie") + "-" + String.format("%08d", invoice.get("number"));
            addHeader(doc, "COMPROBANTE DE PAGO", code);

            // Datos del paciente y sede
            doc.add(new Paragraph("Sede: " + invoice.get("sede_name")));
            doc.add(new Paragraph("Paciente: " + invoice.get("patient_name") +
                    " | " + invoice.get("doc_type") + ": " + invoice.get("doc_number")));
            LocalDateTime createdAt = (LocalDateTime) invoice.get("created_at");
            doc.add(new Paragraph("Fecha: " + (createdAt != null ? createdAt.format(DATE_FMT) : ""))
                    .setMarginBottom(10));

            // Tabla de ítems
            Table table = new Table(UnitValue.createPercentArray(new float[]{5, 2, 2, 2}))
                    .useAllAvailableWidth();
            addHeaderRow(table, "Descripción", "Cantidad", "P. Unit.", "Total");
            for (Map<String, Object> item : items) {
                table.addCell(String.valueOf(item.get("description")));
                table.addCell(String.valueOf(item.get("quantity")));
                table.addCell("S/ " + item.get("unit_price"));
                table.addCell("S/ " + item.get("total"));
            }
            doc.add(table);

            // Totales
            doc.add(new Paragraph("Subtotal: S/ " + invoice.get("subtotal")).setTextAlignment(TextAlignment.RIGHT));
            doc.add(new Paragraph("IGV (18%): S/ " + invoice.get("tax")).setTextAlignment(TextAlignment.RIGHT));
            doc.add(new Paragraph("TOTAL: S/ " + invoice.get("total"))
                    .setBold().setFontSize(13).setTextAlignment(TextAlignment.RIGHT));

            addFooter(doc);
        } catch (Exception e) {
            log.error("Error generando PDF de factura {}: {}", invoiceId, e.getMessage());
            throw new RuntimeException("Error generando PDF de factura", e);
        }
        return out.toByteArray();
    }

    // ─── PRESCRIPCIÓN ─────────────────────────────────────────────────────────

    @Override
    public byte[] generatePrescriptionPdf(UUID prescriptionId) {
        Map<String, Object> rx = jdbcTemplate.queryForMap("""
                SELECT pr.id, pr.status, pr.notes, pr.created_at,
                       p.first_name || ' ' || p.last_name AS patient_name,
                       p.doc_type, p.doc_number,
                       u.full_name AS doctor_name
                FROM prescriptions pr
                JOIN patients p ON pr.patient_id = p.id
                JOIN users u    ON pr.doctor_id  = (SELECT id FROM doctors WHERE user_id = u.id LIMIT 1)
                WHERE pr.id = ?
                """, prescriptionId);

        List<Map<String, Object>> items = jdbcTemplate.queryForList("""
                SELECT m.generic_name, pi.dose, pi.frequency, pi.duration, pi.instructions
                FROM prescription_items pi
                JOIN medications m ON pi.medication_id = m.id
                WHERE pi.prescription_id = ?
                """, prescriptionId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            addHeader(doc, "RECETA MÉDICA", "RX-" + prescriptionId.toString().substring(0, 8).toUpperCase());

            doc.add(new Paragraph("Paciente: " + rx.get("patient_name") +
                    " | " + rx.get("doc_type") + ": " + rx.get("doc_number")));
            doc.add(new Paragraph("Médico: " + rx.get("doctor_name")));
            LocalDateTime createdAt = (LocalDateTime) rx.get("created_at");
            doc.add(new Paragraph("Fecha: " + (createdAt != null ? createdAt.format(DATE_FMT) : ""))
                    .setMarginBottom(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 3}))
                    .useAllAvailableWidth();
            addHeaderRow(table, "Medicamento", "Dosis", "Frecuencia", "Duración", "Indicaciones");
            for (Map<String, Object> item : items) {
                table.addCell(str(item.get("generic_name")));
                table.addCell(str(item.get("dose")));
                table.addCell(str(item.get("frequency")));
                table.addCell(str(item.get("duration")));
                table.addCell(str(item.get("instructions")));
            }
            doc.add(table);

            if (rx.get("notes") != null) {
                doc.add(new Paragraph("Observaciones: " + rx.get("notes")).setMarginTop(10));
            }
            addFooter(doc);
        } catch (Exception e) {
            log.error("Error generando PDF de prescripción {}: {}", prescriptionId, e.getMessage());
            throw new RuntimeException("Error generando PDF de prescripción", e);
        }
        return out.toByteArray();
    }

    // ─── ORDEN DE EXAMEN ──────────────────────────────────────────────────────

    @Override
    public byte[] generateExamOrderPdf(UUID examOrderId) {
        Map<String, Object> order = jdbcTemplate.queryForMap("""
                SELECT eo.id, eo.status, eo.notes, eo.created_at,
                       p.first_name || ' ' || p.last_name AS patient_name,
                       p.doc_type, p.doc_number,
                       u.full_name AS doctor_name
                FROM exam_orders eo
                JOIN patients p ON eo.patient_id = p.id
                JOIN users u    ON eo.doctor_id  = (SELECT id FROM doctors WHERE user_id = u.id LIMIT 1)
                WHERE eo.id = ?
                """, examOrderId);

        List<Map<String, Object>> items = jdbcTemplate.queryForList("""
                SELECT sv.name AS service_name, eoi.status, eoi.result_text, eoi.result_at
                FROM exam_order_items eoi
                JOIN services sv ON eoi.service_id = sv.id
                WHERE eoi.order_id = ?
                """, examOrderId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(out);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf)) {

            addHeader(doc, "ORDEN DE EXAMEN", "EX-" + examOrderId.toString().substring(0, 8).toUpperCase());

            doc.add(new Paragraph("Paciente: " + order.get("patient_name") +
                    " | " + order.get("doc_type") + ": " + order.get("doc_number")));
            doc.add(new Paragraph("Médico solicitante: " + order.get("doctor_name")));
            LocalDateTime createdAt = (LocalDateTime) order.get("created_at");
            doc.add(new Paragraph("Fecha solicitud: " + (createdAt != null ? createdAt.format(DATE_FMT) : ""))
                    .setMarginBottom(10));

            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 4}))
                    .useAllAvailableWidth();
            addHeaderRow(table, "Examen", "Estado", "Resultado");
            for (Map<String, Object> item : items) {
                table.addCell(str(item.get("service_name")));
                table.addCell(str(item.get("status")));
                table.addCell(str(item.get("result_text")));
            }
            doc.add(table);
            addFooter(doc);
        } catch (Exception e) {
            log.error("Error generando PDF de examen {}: {}", examOrderId, e.getMessage());
            throw new RuntimeException("Error generando PDF de examen", e);
        }
        return out.toByteArray();
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private void addHeader(Document doc, String title, String code) {
        doc.add(new Paragraph("CLÍNICA YOSELIN")
                .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph(title + " — " + code)
                .setFontSize(12).setTextAlignment(TextAlignment.CENTER).setMarginBottom(15));
    }

    private void addHeaderRow(Table table, String... headers) {
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    private void addFooter(Document doc) {
        doc.add(new Paragraph("\nDocumento generado electrónicamente — Clínica Yoselin")
                .setFontSize(8).setTextAlignment(TextAlignment.CENTER).setMarginTop(20));
    }

    private String str(Object val) {
        return val != null ? val.toString() : "";
    }
}
