package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.*;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.MemberRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.chat.group.SetMemBerNickNameRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/groups")
public class GroupController {
  private final GroupPortInput groupPortInput;

  @PostMapping
  public ResponseEntity<Object> createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.CREATED, groupPortInput.createGroup(createGroupRequest));
  }

  @PatchMapping("/name")
  public ResponseEntity<Object> updateGroupName(@RequestBody @Valid UpdateGroupRequest updateGroupRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.changeGroupName(updateGroupRequest));
  }

  @PatchMapping("/{groupId}/background")
  public ResponseEntity<Object> updateGroupBackground(
          @PathVariable String groupId,
          @RequestParam("image") @Valid @NotNull(message = "Image cannot be null") MultipartFile image
  ) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.changeGroupBackground(image, groupId));
  }

  @PostMapping("/members")
  public ResponseEntity<Object> addMemberToGroup(@RequestBody @Valid MemberRequest memberRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.addMemberToGroup(memberRequest));
  }

  @DeleteMapping("/members")
  public ResponseEntity<Object> removeMemberFromGroup(@RequestBody @Valid MemberRequest memberRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.kickMemberToGroup(memberRequest));
  }

  @DeleteMapping("/{groupId}/members/me")
  public ResponseEntity<Object> leaveGroup(@PathVariable String groupId) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.outGroup(groupId));
  }

  @PatchMapping("/members/nickname")
  public ResponseEntity<Object> updateMemberNickname(@RequestBody @Valid SetMemBerNickNameRequest setMemberNickNameRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.changeNickname(setMemberNickNameRequest));
  }

  @PatchMapping("/{groupId}")
  public ResponseEntity<Object> updateGroupState(
          @PathVariable String groupId,
          @RequestParam String state
  ) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.changeStateGroup(groupId, state));
  }

  @GetMapping
  public ResponseEntity<Object> getMyGroups(@ModelAttribute PaginationRequest paginationRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.getMyGroups(paginationRequest));
  }
}