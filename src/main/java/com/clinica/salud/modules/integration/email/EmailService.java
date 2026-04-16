package com.clinica.salud.modules.integration.email;

/**
 * Puerto de servicio de email para notificaciones clínicas.
 * La implementación por defecto usa SMTP (Spring Mail).
 */
public interface EmailService {

    /**
     * Recordatorio de cita 24h antes.
     *
     * @param to            email del destinatario
     * @param patientName   nombre completo del paciente
     * @param doctorName    nombre del médico
     * @param appointmentAt fecha y hora de la cita (formateada)
     * @param sedeName      nombre de la sede
     */
    void sendAppointmentReminder(String to, String patientName, String doctorName,
                                  String appointmentAt, String sedeName);

    /**
     * Notificación de resultado de examen disponible.
     *
     * @param to          email del destinatario
     * @param patientName nombre completo del paciente
     * @param examName    nombre del examen
     */
    void sendExamResultReady(String to, String patientName, String examName);

    /**
     * Envío de comprobante de pago.
     *
     * @param to          email del destinatario
     * @param patientName nombre completo del paciente
     * @param invoiceCode código del comprobante (ej. B001-00023)
     * @param total       monto total formateado
     */
    void sendInvoiceReceipt(String to, String patientName, String invoiceCode, String total);
}
