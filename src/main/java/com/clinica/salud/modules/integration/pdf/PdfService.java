package com.clinica.salud.modules.integration.pdf;

import java.util.UUID;

/**
 * Puerto de generación de documentos PDF.
 * La implementación usa iText 7.
 */
public interface PdfService {

    /**
     * Genera el PDF de una factura/boleta.
     *
     * @param invoiceId ID de la factura
     * @return bytes del PDF generado
     */
    byte[] generateInvoicePdf(UUID invoiceId);

    /**
     * Genera el PDF de una prescripción médica.
     *
     * @param prescriptionId ID de la prescripción
     * @return bytes del PDF generado
     */
    byte[] generatePrescriptionPdf(UUID prescriptionId);

    /**
     * Genera el PDF de una orden de examen con resultados.
     *
     * @param examOrderId ID de la orden de examen
     * @return bytes del PDF generado
     */
    byte[] generateExamOrderPdf(UUID examOrderId);
}
