package com.GHTK.Social_Network.infrastructure.adapter.input;


import com.GHTK.Social_Network.application.port.input.SearchPortInput;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/search"})
@RequiredArgsConstructor
public class SearchController {
  private final SearchPortInput searchPortInput;

  @GetMapping("")
  public ResponseEntity<Object> search(@RequestParam String q, @RequestParam(required = false) Integer s) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, searchPortInput.searchPublic(q, s));
  }
}
