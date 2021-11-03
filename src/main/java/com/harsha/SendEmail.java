package com.harsha;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

   public static void send(String text) {    
      // Recipient's email ID needs to be mentioned.
      String to = "harsha.anirudh@gmail.com,rohitsai31997@gmail.com,atl.abhi98@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "no.reply.vaccinetracker@gmail.com";
      String pass = "vaccine@123";

      // Assuming you are sending email from localhost
      String host = "smtp.gmail.com";

      // Get system properties
      Properties prop = System.getProperties();

      // Setup mail server
//      Properties prop = new Properties();
      prop.put("mail.smtp.auth", true);
      prop.put("mail.smtp.starttls.enable", "true");
      prop.put("mail.smtp.host", host);
      prop.put("mail.smtp.port", "587");
      prop.put("mail.smtp.user", from);
      prop.put("mail.smtp.password", pass);
      prop.put("mail.smtp.ssl.trust", host);
      

      Session session = Session.getDefaultInstance(prop);
      try {
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
//         new InternetAddress(to)
         message.addRecipients(Message.RecipientType.TO, to);
         // Set Subject: header field
         String time=LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
         message.setSubject("Class Alert at " +time +" !!!");

         // Now set the actual message
         message.setText(text);
         
         
         Transport transport = session.getTransport("smtp");
         transport.connect(host, from, pass);
         transport.sendMessage(message, message.getAllRecipients());
         transport.close();
         System.out.println("Sent message successfully....");
      } catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}