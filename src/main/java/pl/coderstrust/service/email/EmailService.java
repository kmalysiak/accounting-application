package pl.coderstrust.service.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Messages;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

  private static final String COMPANY_EMAIL_ADDRESS = "green.team.coderstrust@gmail.com";
  private final Logger logger = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender javaMailSender;

  @Autowired
  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  void sendEmail(String emailAddress, String title, String content) {
    MimeMessage mailMessage = javaMailSender.createMimeMessage();

    try {
      MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
      helper.setTo(emailAddress);
      helper.setReplyTo(COMPANY_EMAIL_ADDRESS);
      helper.setFrom(COMPANY_EMAIL_ADDRESS);
      helper.setSubject(title);
      helper.setText(content, true);

    } catch (MessagingException messagingException ) {
      logger.warn(Messages.EMAIL_CANT_BE_SEND + emailAddress, messagingException);
    }
    javaMailSender.send(mailMessage);
  }
}