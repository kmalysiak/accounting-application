package pl.coderstrust.service.email;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmailServiceTest {

  @Rule
  public GreenMailRule server =
      new GreenMailRule(new ServerSetup(3025, "localhost", "smtp"));

  @Autowired
  EmailService emailService;

  @Test
  public void shouldSendEmail() {
    // Given
    String email = "green.team.coderstrust@gmail.com";
    String title = "Test Title";
    String content = "Test Content";
    // When
    emailService.sendEmail(email, title, content);
    // Then
    MimeMessage[] receivedMessages = server.getReceivedMessages();
    assertThat(receivedMessages.length, is(equalTo(1)));
  }
}