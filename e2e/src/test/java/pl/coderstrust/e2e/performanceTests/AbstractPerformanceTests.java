package pl.coderstrust.e2e.performanceTests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.coderstrust.e2e.TestsConfiguration;
import pl.coderstrust.e2e.model.Company;
import pl.coderstrust.e2e.model.Invoice;
import pl.coderstrust.e2e.testHelpers.ObjectMapperHelper;
import pl.coderstrust.e2e.testHelpers.TestCasesGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public abstract class AbstractPerformanceTests {

    protected TestsConfiguration config = new TestsConfiguration();
    protected TestCasesGenerator generator = new TestCasesGenerator();
    protected ObjectMapperHelper mapper = new ObjectMapperHelper();
    protected LocalDate currentDate = LocalDate.now();
    protected Invoice testInvoice;
    protected ArrayList<Invoice> testInvoices = new ArrayList<>();
    private int THREADS_NUMBER = 5;

    protected abstract List<Invoice> getAllInvoicesFromDatabase();

    protected abstract String getInvoicePath();

    protected abstract List<Company> getAllCompaniesFromDatabase();

    protected abstract String getInvoicePathWithInvoiceId(long invoiceId);

    protected abstract long addInvoice(Invoice testInvoice);

    protected abstract String getInvoicePathWithDateRange(LocalDate dateFrom, LocalDate dateTo);

    @Test
    public void shouldReturnCorrectStatusCodeWhenServiceIsUp() {
        given()
                .when()
                .get(getInvoicePath())

                .then()
                .statusCode(config.getServerOkStatusCode());
    }

    @Test
    public void shouldCorrectlyAddAndGetInvoiceByIdinThreads() {

        ArrayList<String> invoicesAdded = new ArrayList<>();
        ArrayList<String> invoicesGetted = new ArrayList<>();
        Runnable test = () -> {

            long invoiceId = addInvoice(testInvoice);
            testInvoice.setId(invoiceId);
            invoicesAdded.add(mapper.toJson(testInvoice));

            String response = given()
                    .body(testInvoice)
                    .get(getInvoicePathWithInvoiceId(invoiceId))
                    .body().print();
            invoicesGetted.add(response);
        };

        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            newFixedThreadPool.submit(test);
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        newFixedThreadPool.shutdown();

        assertThat(invoicesAdded, containsInAnyOrder(invoicesGetted.toArray()));
    }


    @Test
    public void shouldCorrectlyUpdateInvoiceInThreads() {
        ArrayList<String> invoicesUpdated = new ArrayList<>();
        ArrayList<String> invoicesGetted = new ArrayList<>();

        Runnable test = () -> {

            long invoiceId = addInvoice(testInvoice);
            Invoice updatedInvoice = generator.getTestInvoice(
                    config.getDefaultTestInvoiceNumber() + 1, config.getDefaultEntriesCount());
            updatedInvoice.setId(invoiceId);
            updatedInvoice.setBuyer(testInvoice.getBuyer());
            updatedInvoice.setSeller(testInvoice.getSeller());
            updatedInvoice.setName("UPDATED INVOICE");
            invoicesUpdated.add(mapper.toJson(updatedInvoice));

            given()
                    .contentType("application/json")
                    .body(updatedInvoice)
                    .when()
                    .put(getInvoicePathWithInvoiceId(invoiceId));


            String response = given()
                    .body(updatedInvoice)
                    .get(getInvoicePathWithInvoiceId(invoiceId))
                    .body().print();
            invoicesGetted.add(response);
        };

        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            newFixedThreadPool.submit(test);
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        newFixedThreadPool.shutdown();

        assertThat(invoicesGetted, containsInAnyOrder(invoicesUpdated.toArray()));

    }

    @Test
    public void shouldCorrectlyDeleteInvoiceByIdInThreads() {

        List<Long> ids = new ArrayList();
        List<String> response = new ArrayList<>();
        List<String> expected = new ArrayList<>();

        Runnable test = () -> {
            for (int i = 0; i < 10; i++) {
                expected.add("");
                long invoiceId = addInvoice(testInvoice);
                ids.add(invoiceId);
                given()
                        .contentType("application/json")
                        .body(testInvoice)
                        .when()
                        .delete(getInvoicePathWithInvoiceId(invoiceId));
            }
        };

        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            newFixedThreadPool.submit(test);
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        newFixedThreadPool.shutdown();

        for (Long id : ids) {
            String respone = given()
                    .when()
                    .get(getInvoicePathWithInvoiceId(id))
                    .body().print();
            response.add(respone);
        }
        Assert.assertEquals(expected, response);
    }

    @Test
    public void shouldAddSeveralInvoicesinThreadsAndCheckDatabaseSize() {
        long expectedDatabaseSize = allId(getAllInvoicesFromDatabase()).size() + 50;
        Runnable test = () -> {
            for (int i = 0; i < config.getTestInvoicesCount(); i++) {
                given()
                        .contentType("application/json")
                        .body(testInvoice)
                        .when()
                        .post(getInvoicePath());
            }
        };
        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            newFixedThreadPool.submit(test);
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        newFixedThreadPool.shutdown();

        long actualDatabaseSize = allId(getAllInvoicesFromDatabase()).size();
        Assert.assertEquals(actualDatabaseSize, expectedDatabaseSize);
    }

    @Test
    public void shouldAddSeveralInvoicesAndReturnCorrectMessage() {

        List<String> responseList = new ArrayList<>();

        Runnable test = () -> {

            for (int i = 0; i < config.getTestInvoicesCount(); i++) {
                String response = given()
                        .contentType("application/json")
                        .body(testInvoice)
                        .when()
                        .post(getInvoicePath())
                        .body().print();
                responseList.add(response);
            }
        };

        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            newFixedThreadPool.submit(test);
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        newFixedThreadPool.shutdown();

        for (String s : responseList) {
            assertThat(s, containsString("Entry added under id :"));
        }
    }

    @Test(dataProvider = "validDates")
    public void shouldAddSeveralInvoicesInThreadsAndFindThemByIssueDate(LocalDate newDate) {

        int invoicesAtDateCount = getInvoicesCountForDateRange(newDate, newDate);
        testInvoice.setIssueDate(newDate);

        Runnable test = () -> given()
                .contentType("application/json")
                .body(testInvoice)
                .when()
                .post(getInvoicePath());

        final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        for (int i = 0; i < THREADS_NUMBER; i++) {
            newFixedThreadPool.submit(test);
        }
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        newFixedThreadPool.shutdown();

        int invoicesAdded = getInvoicesCountForDateRange(newDate, newDate) - invoicesAtDateCount;
        Assert.assertEquals(invoicesAdded, 5);
    }


    @DataProvider(name = "validDates")
    public Object[] validDatesProvider() {
        Object[] validDates = new Object[10];
        for (int i = 0; i < config.getTestInvoicesCount(); i++) {
            validDates[i] = LocalDate.now().plusYears(i);
        }
        return validDates;
    }

    private int getInvoicesCountForDateRange(LocalDate dateFrom, LocalDate dateTo) {
        String path = getInvoicePathWithDateRange(dateFrom, dateTo);
        String response = given()
                .get(path)
                .body().print();
        return mapper.toInvoiceList(response).size();
    }

    private Set allId(List<Invoice> listToCount) {
        List<Invoice> idsAll = listToCount;
        Set ids = new HashSet();
        for (Invoice i : idsAll) {
            ids.add(i.getId());
        }
        return ids;
    }
}