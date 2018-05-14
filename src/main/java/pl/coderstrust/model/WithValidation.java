package pl.coderstrust.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface WithValidation {

  List<String> validate();

  default List<String> checkDate(LocalDate date) {
    if (date == null) {
      return Arrays.asList(Messages.DATE_EMPTY);
    }
    List<String> errors = new ArrayList<>();
    if (date.isBefore(LocalDate.now())) {
      errors.add(Messages.DATE_TOO_EARLY);
    }
    return errors;
  }

  default boolean checkInputString(String input) {
    return ((input == null || input.trim().length() == 0));
  }
}
