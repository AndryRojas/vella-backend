package co.vellatech.vella_backend.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.mail.admin-notification-to}")
    private String adminNotificationTo;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAdminNewCustomerNotification(String customerName, String customerEmail, String storeSlug) {
        send(adminNotificationTo,
                "Nuevo registro en " + storeSlug,
                "Un nuevo cliente se registró en la tienda '" + storeSlug + "'.\n\n"
                        + "Nombre: " + customerName + "\n"
                        + "Correo: " + customerEmail);
    }

    public void sendVerificationCode(String toEmail, String code) {
        send(toEmail,
                "Verifica tu cuenta en VellaTech",
                "Tu código de verificación es: " + code + "\n\n"
                        + "Este código vence en 15 minutos. Si no solicitaste esto, ignora este correo.");
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("No se pudo enviar correo a {}: {}", to, e.getMessage());
        }
    }
}
