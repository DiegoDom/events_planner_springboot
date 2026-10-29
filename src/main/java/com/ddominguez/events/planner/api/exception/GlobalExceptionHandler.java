package com.ddominguez.events.planner.api.exception;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return errorResponse(HttpStatus.BAD_REQUEST, "Bad request", "Validation errors", errors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    Throwable cause = ex.getCause();
    Map<String, String> errors = new LinkedHashMap<>();

    if (cause instanceof InvalidFormatException invalidFormat) {
      errors.put(lastFieldName(invalidFormat), invalidValueMessage(invalidFormat));
      return errorResponse(HttpStatus.BAD_REQUEST, "Bad request", "Validation errors", errors);
    }

    if (cause instanceof MismatchedInputException mismatchedInput) {
      errors.put(lastFieldName(mismatchedInput), "Invalid value for field");
      return errorResponse(HttpStatus.BAD_REQUEST, "Bad request", "Validation errors", errors);
    }

    return errorResponse(HttpStatus.BAD_REQUEST, "Bad request", "Malformed JSON request", errors);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex) {
    Map<String, String> errors = new LinkedHashMap<>();
    errors.put(
        ex.getName(), "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'");

    return errorResponse(
        HttpStatus.BAD_REQUEST, "Bad request", "Invalid request parameter", errors);
  }

  @ExceptionHandler(NoFieldsToUpdateException.class)
  public ResponseEntity<Object> handleNoFieldsToUpdate(NoFieldsToUpdateException ex) {
    return errorResponse(
        HttpStatus.BAD_REQUEST, "Bad request", ex.getMessage(), new LinkedHashMap<>());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
    return errorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), new LinkedHashMap<>());
  }

  private ResponseEntity<Object> errorResponse(
      HttpStatus status, String error, String message, Map<String, String> errors) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", status.value());
    body.put("error", error);
    body.put("message", message);
    body.put("errors", errors);

    return new ResponseEntity<>(body, status);
  }

  private String invalidValueMessage(InvalidFormatException ex) {
    if (ex.getTargetType() == LocalDate.class) {
      return "Invalid date format. Use ISO-8601 (yyyy-MM-dd), e.g. 2026-10-31";
    }

    String targetType = ex.getTargetType() == null ? "unknown" : ex.getTargetType().getSimpleName();
    return "Invalid value '" + ex.getValue() + "' for type " + targetType;
  }

  private String lastFieldName(JacksonException ex) {
    List<JacksonException.Reference> path = ex.getPath();
    if (path != null) {
      for (int index = path.size() - 1; index >= 0; index--) {
        String propertyName = path.get(index).getPropertyName();
        if (propertyName != null && !propertyName.isBlank()) {
          return propertyName;
        }
      }
    }

    return "body";
  }
}
