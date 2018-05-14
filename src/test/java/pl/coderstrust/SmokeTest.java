package pl.coderstrust;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.coderstrust.service.InvoiceController;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmokeTest {

  @Autowired
  InvoiceController invoiceBookController;

  @Test
  public void contextLoads() {
    assertThat(invoiceBookController, is(not(nullValue())));
  }


}
