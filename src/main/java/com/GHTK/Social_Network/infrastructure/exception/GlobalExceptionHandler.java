package com.GHTK.Social_Network.infrastructure.exception;

import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.UnsupportedEncodingException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleBindException(MethodArgumentNotValidException e) {
    String errorMessage = "Invalid request";

    if (e.getBindingResult().hasErrors()) {
      FieldError fieldError = e.getBindingResult().getFieldError();
      if (fieldError != null) {
        errorMessage = fieldError.getDefaultMessage();
      }
    }
    return ResponseHandler.generateErrorResponse(errorMessage, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = CustomException.class)
  public ResponseEntity<Object> handleCustomException(CustomException e) {
    return ResponseHandler.generateErrorResponse(e.getMessage(), e.getHttpStatus());
  }

  @ExceptionHandler({MessagingException.class, UnsupportedEncodingException.class})
  public ResponseEntity<Object> handleEmailException(Exception ex) {
    return ResponseHandler.generateErrorResponse("Error sending email: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneralException(Exception ex) {
    log.error(ex.getMessage(), ex);
    return ResponseHandler.generateErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
