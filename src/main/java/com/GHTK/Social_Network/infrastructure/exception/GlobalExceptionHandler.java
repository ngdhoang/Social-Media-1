package com.GHTK.Social_Network.infrastructure.exception;

import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(value = Exception.class)
  ResponseEntity<Object> handleRuntimeException(RuntimeException e) {
    return ResponseHandler.generateErrorResponse(e);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleBindException(MethodArgumentNotValidException e) {
    String errorMessage = "Invalid request";

    if (e.getBindingResult().hasErrors()) {
      FieldError fieldError = e.getBindingResult().getFieldError();
      if (fieldError != null) {
        errorMessage = fieldError.getDefaultMessage();
      }
    }
    return ResponseHandler.generateErrorResponse(errorMessage);
  }

}
