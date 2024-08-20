package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class GroupController {
  private final GroupPortInput groupPortInput;

  @PostMapping("/group/create")
  public ResponseEntity<Object> createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.createGroup(createGroupRequest));
  }

  @PutMapping("/group/add-member")
  public ResponseEntity<Object> addNewMemberToGroup(@RequestBody @Valid MemberRequest memberRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.addMemberToGroup(memberRequest));
  }

  @PutMapping("/group/out-group/{groupId}")
  public ResponseEntity<Object> outGroup(@PathVariable @Valid String groupId){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.outGroup(groupId));
  }

  @PutMapping("/group/set-nickname")
  public ResponseEntity<Object> setMemberNickName(@RequestBody @Valid SetMemBerNickNameRequest setMemBerNickNameRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK,groupPortInput.changeNickname(setMemBerNickNameRequest));
  }

  @GetMapping("/group/get-my-groups")
  public ResponseEntity<Object> getMyGroups(@ModelAttribute PaginationRequest paginationRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK , groupPortInput.getMyGroups(paginationRequest));
  }
}
