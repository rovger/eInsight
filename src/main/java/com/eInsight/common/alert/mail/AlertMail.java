package com.eInsight.common.alert.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class AlertMail {
    private static final Properties MAIL_PROPS = new Properties();

    static {
        MAIL_PROPS.put("mail.transport.protocol", "smtp" );
        MAIL_PROPS.put("mail.smtp.host", "smtp.163.com");
        MAIL_PROPS.put("mail.smtp.port", "25");
    }

    private static final Session SESSION = Session.getDefaultInstance(MAIL_PROPS, null);

    public static void sendMail(String from, String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        Message msg = buidMessage(subject, body);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        javax.mail.Transport.send(msg);
    }

    private static Message buidMessage(String subject, String body) throws UnsupportedEncodingException, MessagingException {
        Message msg = new javax.mail.internet.MimeMessage(SESSION);

        msg.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));

        MimeMultipart topLevelPart = new MimeMultipart("mixed");
        MimeBodyPart textBodyPart = new MimeBodyPart();

        textBodyPart.setContent(body, "text/html;charset=UTF-8");
        topLevelPart.addBodyPart(textBodyPart);
        msg.setContent(topLevelPart);
        return msg;
    }
}