package pl.coderstrust.service.taxservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Payment;
import pl.coderstrust.model.PaymentType;
import pl.coderstrust.service.CompanyService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PaymentService {

  private CompanyService companyService;

  @Autowired
  public PaymentService(CompanyService companyService) {
    this.companyService = companyService;
  }

  public long addPayment(long companyId, Payment payment) {
    long id = getNextId(companyId);
    payment.setId(id);
    addPaymentToList(companyId, payment);
    return id;
  }

  public Payment getPayment(long companyId, long paymentId) {
    List<Payment> paymentsToSearch = getPayments(companyId);
    int paymentIndex = getPaymentIndex(paymentId, paymentsToSearch);
    return paymentsToSearch.get(paymentIndex);
  }

  public List<Payment> getPayments(long companyId) {
    return companyService.findEntry(companyId).getPayments();
  }

  public List<Payment> getPaymentsByDate(long companyId, LocalDate startDate, LocalDate endDate) {
    if (startDate == null) {
      startDate = LocalDate.of(1000, 1, 1);
    }
    if (endDate == null) {
      endDate = LocalDate.of(3000, 1, 1);
    }

    return getPayments(companyId)
        .stream()
        .filter(isBetweenDates(startDate.minusDays(1), endDate.plusDays(1)))
        .collect(Collectors.toList());
  }

  public List<Payment> getPaymentsByTypeAndDate(long companyId,
      LocalDate startDate, LocalDate endDate, PaymentType type) {
    if (startDate == null) {
      startDate = LocalDate.of(1000, 1, 1);
    }
    if (endDate == null) {
      endDate = LocalDate.of(3000, 1, 1);
    }

    return getPaymentsByDate(companyId, startDate, endDate)
        .stream()
        .filter(isExpectedType(type))
        .collect(Collectors.toList());
  }

  public void updatePayment(long companyId, Payment payment) {
    List<Payment> payments = getPayments(companyId);
    for (int i = 0; i < payments.size(); i++) {
      if (payments.get(i).getId() == payment.getId()) {
        payments.set(i, payment);
        break;
      }
    }
    Company company = companyService.findEntry(companyId);
    company.setPayments(payments);
    companyService.updateEntry(company);
  }

  public void deletePayment(long companyId, long paymentId) {
    List<Payment> payments = getPayments(companyId);
    for (int i = 0; i < payments.size(); i++) {
      if (payments.get(i).getId() == paymentId) {
        payments.remove(i);
        break;
      }
    }
    Company company = companyService.findEntry(companyId);
    company.setPayments(payments);
    companyService.updateEntry(company);
  }

  public boolean idExist(long companyId, long paymentId) {
    return getPayments(companyId)
        .stream()
        .anyMatch(payment -> payment.getId() == paymentId);
  }

  private long getNextId(long companyId) {
    Optional<Long> id = companyService.findEntry(companyId).getPayments()
        .stream()
        .map(Payment::getId)
        .max(Long::compareTo);
    return id.isPresent() ? id.get() + 1 : 1;
  }

  private void addPaymentToList(long companyId, Payment payment) {
    List<Payment> payments = companyService.findEntry(companyId).getPayments();
    payments.add(payment);
    Company company = companyService.findEntry(companyId);
    company.setPayments(payments);
    companyService.updateEntry(company);

  }

  private int getPaymentIndex(long paymentId, List<Payment> payments) {
    for (int i = 0; i < payments.size(); i++) {
      if (payments.get(i).getId() == paymentId) {
        return i;
      }
    }
    return -1;
  }

  Predicate<Payment> isBetweenDates(LocalDate startDate, LocalDate endDate) {
    return x ->
        (x.getIssueDate().isAfter(startDate.minusDays(1))
            && x.getIssueDate().isBefore(endDate.plusDays(1)));
  }

  Predicate<Payment> isExpectedType(PaymentType type) {
    return x -> x.getType() == type;
  }
}

