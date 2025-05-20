package com.wanted.assignment.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValidationErrorType {

  NOT_BLANK("NotBlank"),
  NOT_EMPTY("NotEmpty"),
  NOT_NULL("NotNull"),
  MAX("Max"),
  MIN("Min");

  private final String code;

  public static boolean isValidationError(String code) {
    for (ValidationErrorType errorType : values()) {
      if (errorType.getCode().equals(code)) {
        return true;
      }
    }
    return false;
  }
}
