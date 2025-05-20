package com.wanted.assignment.common.validator;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PasswordValidatorTest {

  private static Validator validator;

  @BeforeAll
  static void setup() {
    try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
      validator = factory.getValidator();
    }
  }

  static class PasswordWrapper {

    @Password
    private final String password;

    public PasswordWrapper(String password) {
      this.password = password;
    }
  }

  private boolean isPasswordValid(String password) {
    return validator.validate(new PasswordWrapper(password)).isEmpty();
  }

  @DisplayName("유효한 비밀번호일 경우 검증 성공")
  @Test
  void testValidPassword() {
    assertThat(isPasswordValid("Password1!")).isTrue(); // 대문자 + 소문자 + 숫자 + 특수문자
    assertThat(isPasswordValid("abcd1234@")).isTrue();  // 소문자 + 숫자 + 특수문자
    assertThat(isPasswordValid("ABCdef12")).isTrue();   // 대문자 + 소문자 + 숫자
  }

  @Test
  @DisplayName("비밀번호가 null 또는 빈 문자열인 경우 검증 실패")
  void testNullOrBlankPassword() {
    assertThat(isPasswordValid(null)).isFalse();
    assertThat(isPasswordValid("")).isFalse();
  }

  @Test
  @DisplayName("비밀번호가 길이가 8자 미만 또는 16자 초과인 경우 검증 실패")
  void testInvalidPasswordLength() {
    assertThat(isPasswordValid("Passwo!")).isFalse();
    assertThat(isPasswordValid("PasswordPassword!")).isFalse();
  }

  @DisplayName("비밀번호가 필수 문자 유형을 한 가지 또는 두 가지만 포함한 경우 검증 실패")
  @Test
  void testPasswordWithInsufficientCharacterTypes() {
    assertThat(isPasswordValid("abcdefgh")).isFalse();  // 소문자
    assertThat(isPasswordValid("abcdefgh!")).isFalse(); // 소문자 + 특수문자
    assertThat(isPasswordValid("ABCDEFGH")).isFalse();  // 대문자
    assertThat(isPasswordValid("ABCDEFGH!")).isFalse(); // 대문자 + 특수문자
    assertThat(isPasswordValid("12345678")).isFalse();  // 숫자
    assertThat(isPasswordValid("12345678a")).isFalse(); // 숫자 + 소문자
    assertThat(isPasswordValid("!@#$%^&*")).isFalse();  // 특수문자
    assertThat(isPasswordValid("!@#$%^&*A")).isFalse(); // 특수문자 + 대문자
  }
}
