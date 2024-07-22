package com.GHTK.Social_Network.infrastructure.payload.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
  public static final String MESSAGE_SUCCESS = "OK";
  public static final String MESSAGE_FAIL = "FAIL";

  public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj) {
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("message", message);
    body.put("status", status.value());
    body.put("data", responseObj);

    return new ResponseEntity<Object>(body, status);
  }

  public static ResponseEntity<Object> notFoundResponse() {
    String message = "Not found";
    return ResponseHandler.generateResponse(message, HttpStatus.MULTI_STATUS, null);
  }

  public static ResponseEntity<Object> generateErrorResponse(String e, HttpStatus status) {
    return ResponseHandler.generateResponse(e, status, null);
  }

  public static ResponseEntity<Object> generateErrorResponse(Exception e, HttpStatus status) {
    return ResponseHandler.generateResponse(e.getMessage(), status, null);
  }


}
