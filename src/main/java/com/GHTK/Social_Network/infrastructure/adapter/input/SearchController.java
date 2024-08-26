package com.GHTK.Social_Network.infrastructure.adapter.input;


import com.GHTK.Social_Network.application.port.input.SearchPortInput;
import com.GHTK.Social_Network.infrastructure.adapter.output.entity.node.UserNode;
import com.GHTK.Social_Network.infrastructure.adapter.output.repository.node.UserNodeRepository;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SearchUserRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.internal.value.FloatValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/search"})
@RequiredArgsConstructor
public class SearchController {
  private final SearchPortInput searchPortInput;

  @GetMapping("")
  public ResponseEntity<Object> search(@RequestParam String q, @RequestParam(required = false) Integer s) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, searchPortInput.searchPublic(q, s));
  }

  @GetMapping("/user")
  public ResponseEntity<Object> infer(@ModelAttribute @Valid SearchUserRequest searchUserRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, searchPortInput.searchUser(searchUserRequest));
  }

  @GetMapping("friend")
  public ResponseEntity<Object> searchFriend(@RequestParam(required = false) String keyword, @ModelAttribute @Valid PaginationRequest paginationRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, searchPortInput.searchFriend(keyword, paginationRequest));
  }
}
