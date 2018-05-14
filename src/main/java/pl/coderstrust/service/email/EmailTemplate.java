package pl.coderstrust.service.email;

import org.thymeleaf.context.Context;
import pl.coderstrust.model.EmailTemplateNames;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.SummaryOfProductsPrices;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

class EmailTemplate {

  private Invoice invoice;
  private BigDecimal totalRetailPrice = BigDecimal.valueOf(0);
  private BigDecimal totalNetValue = BigDecimal.valueOf(0);
  private BigDecimal totalVatValue = BigDecimal.valueOf(0);

  EmailTemplate(Invoice invoice) {
    this.invoice = invoice;
  }

  Context template() {
    Context context = new Context();

    context.setVariable(EmailTemplateNames.HEADER, EmailTemplateNames.HEADER_INFO);
    context.setVariable(EmailTemplateNames.NAME, invoice.getName());
    context.setVariable(EmailTemplateNames.PAYMENT_DATE, invoice.getPaymentDate());
    context.setVariable(EmailTemplateNames.ISSUE_DATE, invoice.getIssueDate());
    context.setVariable(EmailTemplateNames.SELLER_NAME, invoice.getSeller().getName());
    context.setVariable(EmailTemplateNames.SELLER_ADDRESS, invoice.getSeller().getAddress());
    context.setVariable(EmailTemplateNames.SELLER_CITY, invoice.getSeller().getCity());
    context.setVariable(EmailTemplateNames.SELLER_ZIP_CODE, invoice.getSeller().getZipCode());
    context.setVariable(EmailTemplateNames.SELLER_NIP, invoice.getSeller().getNip());
    context.setVariable(EmailTemplateNames.SELLER_BANK_ACCOUNT_NUMBER,
        invoice.getSeller().getBankAccoutNumber());
    context.setVariable(EmailTemplateNames.BUYER_NAME, invoice.getBuyer().getName());
    context.setVariable(EmailTemplateNames.BUYER_ADDRESS, invoice.getBuyer().getAddress());
    context.setVariable(EmailTemplateNames.BUYER_CITY, invoice.getBuyer().getCity());
    context.setVariable(EmailTemplateNames.BUYER_ZIP_CODE, invoice.getBuyer().getZipCode());
    context.setVariable(EmailTemplateNames.BUYER_NIP, invoice.getBuyer().getNip());
    context.setVariable(EmailTemplateNames.BUYER_BANK_ACCOUNT_NUMBER,
        invoice.getBuyer().getBankAccoutNumber());
    context.setVariable(EmailTemplateNames.PRODUCTS, pricesList(invoice));
    context.setVariable(EmailTemplateNames.TOTAL_RETAIL_PRICE, totalRetailPrice);
    context.setVariable(EmailTemplateNames.TOTAL_NET_VALUE, totalNetValue);
    context.setVariable(EmailTemplateNames.TOTAL_VAT_VALUE, totalVatValue);
    context.setVariable(EmailTemplateNames.TOTAL_PRICE, totalNetValue.add(totalVatValue));
    return context;
  }

  private List<SummaryOfProductsPrices> pricesList(Invoice invoice) {
    List<InvoiceEntry> products = invoice.getProducts();
    List<SummaryOfProductsPrices> pricesList = new ArrayList<>();

    for (InvoiceEntry product : products) {
      BigDecimal netValue = product.getProduct().getNetValue()
          .setScale(2, RoundingMode.HALF_UP);
      double vatRate = product.getProduct().getVatRate().getVatPercent();
      BigDecimal vatValue = netValue.multiply(BigDecimal.valueOf(vatRate))
          .setScale(2, RoundingMode.HALF_UP);
      int amount = product.getAmount();
      BigDecimal netValueMultiplyAmount = netValue.multiply(BigDecimal.valueOf(amount));
      BigDecimal vatValueMultiplyAmount = vatValue.multiply(BigDecimal.valueOf(amount));

      SummaryOfProductsPrices invoicePrices = new SummaryOfProductsPrices();
      invoicePrices.setProductName(product.getProduct().getName());
      invoicePrices.setAmount(String.valueOf(amount));
      invoicePrices.setRetailPrice(String.valueOf(netValue));
      invoicePrices.setProductNetValue(String.valueOf(netValueMultiplyAmount));
      invoicePrices.setProductVatRate(String.valueOf((int) (vatRate * 100)));
      invoicePrices.setProductVatValue(String.valueOf(vatValueMultiplyAmount));
      invoicePrices.setTotal(
          String.valueOf(netValue.add(vatValue).multiply(BigDecimal.valueOf(amount))));

      totalRetailPrice = totalRetailPrice.add(netValue);
      totalNetValue = totalNetValue.add(netValueMultiplyAmount);
      totalVatValue = totalVatValue.add(vatValueMultiplyAmount);

      pricesList.add(invoicePrices);
    }
    return pricesList;
  }
}