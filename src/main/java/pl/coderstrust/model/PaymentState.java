package pl.coderstrust.model;

public enum PaymentState {
  PAID("Paid"),
  NOT_PAID("Not paid");

  private String state;

  PaymentState() {
  }

  PaymentState(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }
}
