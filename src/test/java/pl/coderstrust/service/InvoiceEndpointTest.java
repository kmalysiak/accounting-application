package pl.coderstrust.service;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.payload;
import static org.springframework.ws.test.server.ResponseMatchers.validPayload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatcher;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import pl.coderstrust.service.soap.Messages;
import pl.coderstrust.service.soap.bindingClasses.InvoiceGetByDateResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceEndpointTest {

  private static final int INVOICES_COUNT = 10;
  private static final String RESOURCES_PATH = "src/test/resources/SoapXmlRequests/";

  @Autowired
  private ApplicationContext applicationContext;
  private MockWebServiceClient mockClient;
  private Resource xsdSchema = new ClassPathResource("invoice.xsd");

  @Before
  public void init() {
    mockClient = MockWebServiceClient.createClient(applicationContext);
  }

  @Test
  public void shouldAddInvoice() throws IOException {
    Source requestPayload = getRequest("invoiceAddRequest.xml");
    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(new ContainsStringMatcher(Messages.NO_ERRORS));
  }

  @Test
  public void shouldGeInvoiceById() throws IOException {

    Source requestPayload = getRequest("invoiceAddRequest.xml");

    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault());

    requestPayload = getRequest("invoiceGetByIdRequest.xml");

    mockClient.sendRequest(withPayload(requestPayload))
        .andExpect(noFault())
        .andExpect(new ContainsStringMatcher(Messages.NO_ERRORS))
        .andExpect(validPayload(xsdSchema));
  }

  @Test
  public void shouldGetInvoicesByDate() throws Exception {
    Source requestPayload = getRequest("invoiceAddRequestDateChanged.xml");

    for (int i = 0; i < INVOICES_COUNT; i++) {
      mockClient
          .sendRequest(withPayload(requestPayload))
          .andExpect(noFault());
    }

    requestPayload = getRequest("invoiceGetByDateRequest.xml");
    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    mockClient.sendRequest(withPayload(requestPayload))
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(
            (request, response) -> response.writeTo(stream));
    assertThat(countInvoicesAtBody(stream.toString()), is(equalTo(INVOICES_COUNT)));
  }

  @Test
  public void shouldUpdateInvoice() throws Exception {
    Source requestPayload = getRequest("invoiceAddRequest.xml");

    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault());

    requestPayload = getRequest("invoiceUpdateRequest.xml");

    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault());

    requestPayload = getRequest("invoiceGetByIdRequest.xml");
    Source responsePayload = getRequest("invoiceUpdateResponse.xml");

    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault())
        .andExpect(validPayload(xsdSchema))
        .andExpect(payload(responsePayload));
  }

  @Test
  public void shouldRemoveInvoice() throws Exception {
    Source requestPayload = getRequest("invoiceAddRequest.xml");

    for (int i = 0; i < INVOICES_COUNT; i++) {
      mockClient
          .sendRequest(withPayload(requestPayload))
          .andExpect(noFault());
    }
    requestPayload = getRequest("invoiceRemoveRequest.xml");

    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault())
        .andExpect(new ContainsStringMatcher(Messages.NO_ERRORS));

    requestPayload = getRequest("invoiceGetByRemovedIdRequest.xml");

    mockClient
        .sendRequest(withPayload(requestPayload))
        .andExpect(noFault())
        .andExpect(new ContainsStringMatcher(Messages.INVOICE_NOT_EXIST));
  }

  private int countInvoicesAtBody(String soapBodyXml) throws Exception {
    SOAPMessage msg = MessageFactory.newInstance().createMessage(null,
        new ByteArrayInputStream(soapBodyXml.getBytes(StandardCharsets.UTF_8)));
    InvoiceGetByDateResponse response = unmarshall(convertToString(msg.getSOAPBody()));
    return response.getInvoiceList().getInvoice().size();
  }

  private InvoiceGetByDateResponse unmarshall(String input) throws Exception {
    JAXBContext context = JAXBContext.newInstance(InvoiceGetByDateResponse.class);
    Unmarshaller um = context.createUnmarshaller();
    return (InvoiceGetByDateResponse) um.unmarshal(new StringReader(input));
  }

  private Source getRequest(String fileName) throws IOException {
    String xmlRequest = xmlFileRead(RESOURCES_PATH + fileName);
    return new StringSource(xmlRequest);
  }

  private String xmlFileRead(String filePath) throws IOException {
    return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
  }

  private String convertToString(SOAPBody message) throws Exception {

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

    Document doc = message.extractContentAsDocument();
    StringWriter sw = new StringWriter();
    transformer.transform(new DOMSource(doc), new StreamResult(sw));
    return sw.toString();
  }

  class ContainsStringMatcher implements ResponseMatcher {

    String searchPhrase;

    private ContainsStringMatcher(String searchPhrase) {
      this.searchPhrase = searchPhrase;
    }

    @Override
    public void match(WebServiceMessage webServiceMessage, WebServiceMessage webServiceMessage1)
        throws IOException, AssertionError {

      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      webServiceMessage1.writeTo(stream);
      if (!stream.toString().contains(searchPhrase)) {
        throw new AssertionError();
      }
    }
  }
}
