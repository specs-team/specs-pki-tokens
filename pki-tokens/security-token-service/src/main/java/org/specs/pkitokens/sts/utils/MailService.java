package org.specs.pkitokens.sts.utils;

import org.specs.specsdb.model.User;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class MailService {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss z";

    public void sendAccountBlockedNotification(User user) throws Exception {
        // TODO: conf parameters
        String smtpHost = "mx.xlab.si";

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", smtpHost);
        Session session = Session.getDefaultInstance(properties);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("support@specs.org", "SPECS Support"));
        msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(user.getEmail()));
        msg.setSubject("Your SPECS account has been locked");

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        msg.setText(String.format("Dear %s %s,\n"
                        + "\n" +
                        "We have detected too many attempts to sign in to your account with the incorrect password. " +
                        "As a security measure, your account has been locked at %s to prevent unauthorized " +
                        "users from being able to access it. To login, please provide the unlock code %s together " +
                        "with your password.\n" +
                        "\n" +
                        "Sincerely,\n" +
                        "SPECS.org",
                user.getFirstName(), user.getLastName(), sdf.format(user.getLockDate()),
                user.getUnlockCode()
        ));

        Transport.send(msg);
    }
}
