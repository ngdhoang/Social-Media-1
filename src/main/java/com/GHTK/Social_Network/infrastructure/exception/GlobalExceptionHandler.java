package com.GHTK.Social_Network.infrastructure.exception;

import com.GHTK.Social_Network.common.customException.CustomException;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleBindException(MethodArgumentNotValidException e) {
    List<String> errors = new ArrayList<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.add(error.getDefaultMessage());
    }
    for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
      errors.add(error.getDefaultMessage());
    }
    return ResponseHandler.generateErrorResponse(errors.get(0), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = CustomException.class)
  public ResponseEntity<Object> handleCustomException(CustomException e) {
    return ResponseHandler.generateErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({MessagingException.class, UnsupportedEncodingException.class})
  public ResponseEntity<Object> handleEmailException(Exception ex) {
    return ResponseHandler.generateErrorResponse("Error sending email: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoResourceFoundException(Exception ex) {
        return ResponseHandler.generateErrorResponse("Resource not found", HttpStatus.NOT_FOUND);
    }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ResponseEntity<Object> handleHttpMessageNotReadableException(Exception ex) {
    return ResponseHandler.generateErrorResponse("Invalid request", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({MultipartException.class})
  public ResponseEntity<Object> handleMultipartException(Exception ex) {
    return ResponseHandler.generateErrorResponse("Invalid file", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
    return ResponseHandler.generateErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGeneralException(Exception ex) {
    log.error(ex.getMessage(), ex);
    return ResponseHandler.generateErrorResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
