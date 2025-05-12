package esprit.tn.guiproject.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    private static final String FROM_EMAIL = "smartmove1101@gmail.com";
    private static final String APP_PASSWORD = "epwgfnytkdbgpbew"; // Mot de passe d'application sans espaces

    public static void sendEmail(String to, String subject, String content) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("Email envoyé avec succès à " + to);
        } catch (MessagingException e) {
            System.err.println("Erreur détaillée lors de l'envoi de l'email : " + e.getMessage());
            throw e;
        }
    }
} 