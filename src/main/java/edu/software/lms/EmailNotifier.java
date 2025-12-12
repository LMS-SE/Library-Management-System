package edu.software.lms;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import io.github.cdimascio.dotenv.Dotenv;

public class EmailNotifier implements Observer {

    private static final Logger logger = Logger.getLogger(EmailNotifier.class.getName());

    /** Stores all formatted messages "sent" by this notifier (for logs/tests). */
    private final List<String> sentMessages = new ArrayList<>();

    // SMTP configuration (nullable -> means "no real sending")
    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String password;
    private final String fromAddress;

    /**
     * Default constructor:
     * - Loads SMTP configuration from .env (via dotenv)
     * - If required values are missing, it will only record messages in memory
     *   and NOT actually send emails.
     */
    public EmailNotifier() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // don't crash if .env is missing
                .load();

        this.smtpHost = dotenv.get("SMTP_HOST");
        String portStr = dotenv.get("SMTP_PORT");
        this.smtpPort = portStr != null ? Integer.parseInt(portStr) : -1;
        this.username = dotenv.get("SMTP_USERNAME");
        this.password = dotenv.get("SMTP_PASSWORD");
        this.fromAddress = dotenv.get("SMTP_FROM");

        if (!isSmtpConfigured()) {
            logger.warning("EmailNotifier: SMTP not fully configured (.env missing or incomplete). " +
                    "Emails will NOT actually be sent, only recorded in sentMessages.");
        }
    }

    /**
     * Full SMTP constructor (optional):
     * use this when you want to pass SMTP config manually (e.g. from tests).
     */
    public EmailNotifier(String smtpHost,
                         int smtpPort,
                         String username,
                         String password,
                         String fromAddress) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.password = password;
        this.fromAddress = fromAddress;
    }

    private boolean isSmtpConfigured() {
        return smtpHost != null &&
                smtpPort > 0 &&
                username != null &&
                password != null &&
                fromAddress != null;
    }

    /**
     * Handles a notification request for a user.
     * - Always records the formatted message in sentMessages.
     * - If SMTP is configured, also sends a real email to user.getEmail().
     *
     * @param user    the recipient user
     * @param message the notification message
     */
    @Override
    public void notify(User user, String message) {
        String formatted = "To: " + user.getEmail() + " | " + message;
        sentMessages.add(formatted);

        if (!isSmtpConfigured()) {
            String logToLog="EmailNotifier: SMTP not configured, only recording message: "+ formatted;
            logger.info(logToLog);
            return;
        }

        sendEmail(user.getEmail(), "Library notification", message);
    }

    /**
     * Returns all messages that were "sent" through this notifier.
     *
     * @return list of recorded messages
     */
    public List<String> getSentMessages() {
        return sentMessages;
    }

    /**
     * Sends an email using Jakarta Mail and the configured SMTP settings.
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true"); // TLS
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", String.valueOf(smtpPort));

            Session session = Session.getInstance(
                    props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    }
            );

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromAddress));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);
            String logToLog="Email sent successfully to " + to;
            logger.info(logToLog);
        } catch (MessagingException e) {
            String logToLog="Failed to send email to " + to;
            logger.log(Level.SEVERE, logToLog, e);
        }
    }
}
