package com.clinica.salud.modules.integration.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:notificaciones@clinica.pe}")
    private String fromAddress;

    @Override
    public void sendAppointmentReminder(String to, String patientName, String doctorName,
                                         String appointmentAt, String sedeName) {
        String subject = "Recordatorio de cita — " + appointmentAt;
        String body = String.format("""
                Estimado/a %s,

                Le recordamos que tiene una cita programada:

                  Médico  : %s
                  Fecha   : %s
                  Sede    : %s

                Por favor, llegar 10 minutos antes con su documento de identidad.

                Atentamente,
                Clínica Yoselin
                """, patientName, doctorName, appointmentAt, sedeName);

        send(to, subject, body);
    }

    @Override
    public void sendExamResultReady(String to, String patientName, String examName) {
        String subject = "Sus resultados de examen están listos";
        String body = String.format("""
                Estimado/a %s,

                Sus resultados del examen "%s" ya están disponibles.
                Puede consultarlos en el Portal del Paciente o acercarse a nuestra sede.

                Atentamente,
                Clínica Yoselin
                """, patientName, examName);

        send(to, subject, body);
    }

    @Override
    public void sendInvoiceReceipt(String to, String patientName, String invoiceCode, String total) {
        String subject = "Comprobante de pago " + invoiceCode;
        String body = String.format("""
                Estimado/a %s,

                Su pago ha sido registrado exitosamente.

                  Comprobante : %s
                  Total       : S/ %s

                Gracias por confiar en Clínica Yoselin.

                Atentamente,
                Clínica Yoselin
                """, patientName, invoiceCode, total);

        send(to, subject, body);
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email enviado a {} — {}", to, subject);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
        }
    }
}
