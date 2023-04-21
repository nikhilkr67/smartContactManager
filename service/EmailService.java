package com.smartcontact.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String subject, String message, String to){
        boolean f= false;
        String from="pankajkrunique1234@gmail.com";

        String host="smtp.gmail.com";

        Properties properties = System.getProperties();
        System.out.println("PROPERTIES"+properties);

        //host set
        properties.put("mail.smtp.host",host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        //get session object
        Session session= Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("krnikhil667@gmail.com","necrusnjwxmmwhmh");
            }
        });
        session.setDebug(true);

        //compose the message
        MimeMessage mimeMessage=new MimeMessage(session);
        try{
            mimeMessage.setFrom(from);

            mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));

            mimeMessage.setSubject(subject);

            //mimeMessage.setText(message);
            mimeMessage.setContent(message,"text/html");


            Transport.send(mimeMessage);

            f = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return f;

    }
}
