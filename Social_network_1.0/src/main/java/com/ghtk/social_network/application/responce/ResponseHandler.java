package com.ghtk.social_network.application.responce;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
  public static final String MESSAGE_SUCCESS = "OK";
  public static final String MESSAGE_FAIL = "FAIL";

  public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Object responseObj){
    Map<String, Object> body = new HashMap<String, Object>();
    body.put("message", message);
    body.put("status", status.value());
    body.put("data", responseObj);

    return new ResponseEntity<Object>(body, status);
  }

  public static ResponseEntity<Object> generateErrorResponse(Exception e){
    return ResponseHandler.generateResponse(MESSAGE_FAIL, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
  }

  public static ResponseEntity<Object> generateErrorResponse(String e){
    return ResponseHandler.generateResponse(MESSAGE_FAIL, HttpStatus.INTERNAL_SERVER_ERROR, e);
  }
}
