package com.wanted.assignment.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

  private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
  private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
  private static final Pattern DIGIT = Pattern.compile("\\d");
  private static final Pattern SPECIAL_CHAR = Pattern.compile("[!@#$%^&*()_+=\\-]");

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {

    if (password == null || password.length() < 8 || password.length() > 16) {
      return false;
    }

    int matchCount = 0;
    if (UPPERCASE.matcher(password).find()) {
      matchCount++;
    }
    if (LOWERCASE.matcher(password).find()) {
      matchCount++;
    }
    if (DIGIT.matcher(password).find()) {
      matchCount++;
    }
    if (SPECIAL_CHAR.matcher(password).find()) {
      matchCount++;
    }

    return matchCount >= 3;
  }
}
