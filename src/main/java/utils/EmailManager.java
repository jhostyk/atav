package utils;

import global.Data;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author nick
 */
public class EmailManager {

    private static String MAIL_SERVER;
    private static String EMAIL_FROM;
    private static String EMAIL_TO;

    public static void init() {
        try {
            String configPath = Data.SYSTEM_CONFIG;

            if (CommonCommand.isDebug) {
                configPath = Data.SYSTEM_CONFIG_FOR_DEBUG;
            }

            InputStream input = new FileInputStream(configPath);
            Properties prop = new Properties();
            prop.load(input);

            MAIL_SERVER = prop.getProperty("mail-server");
            EMAIL_FROM = prop.getProperty("email-from");
            EMAIL_TO = prop.getProperty("email-to");
        } catch (IOException e) {
            ErrorManager.send(e);
        }
    }

    /**
     * Utility method to send simple HTML email
     *
     * @param subject
     * @param body
     */
    public static void sendEmail(String subject, String body) {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", MAIL_SERVER);
            Session session = Session.getInstance(props, null);

            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(EMAIL_FROM, "IGM BIOINFO"));

            msg.setReplyTo(InternetAddress.parse(EMAIL_TO, false));

            msg.setSubject(subject, "UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_TO, false));
            
            Transport.send(msg);
        } catch (UnsupportedEncodingException | MessagingException e) {
        }
    }
}