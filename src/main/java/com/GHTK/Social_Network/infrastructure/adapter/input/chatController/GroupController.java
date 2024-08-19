package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.domain.collection.chat.Member;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.PaginationRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.SetMemBerNickNameRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class GroupController {
  private final GroupPortInput groupPortInput;

  @PostMapping("/group/create")
  public ResponseEntity<Object> createGroup(@RequestBody @Valid CreateGroupRequest createGroupRequest) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.createGroup(createGroupRequest));
  }
  @PutMapping("/group/update")
  public ResponseEntity<Object> updateGroup(@RequestBody @Valid UpdateGroupRequest updateGroupRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.updateGroup(updateGroupRequest));
  }

  @PutMapping("/group/add-member")
  public ResponseEntity<Object> addNewMemberToGroup(String groupId, List<Member> members){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.addNewMemberToGroup(groupId,members));
  }
  @DeleteMapping("/group/delete")
  public  ResponseEntity<Object> deleteGroup(@RequestBody @Valid String groupId){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.deleteGroup(groupId));
  }

  @PutMapping("/group/out-group/{groupId}")
  public ResponseEntity<Object> outGroup(@PathVariable @Valid String groupId){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.outGroup(groupId));
  }

  @PutMapping("/group/set-nickname")
  public ResponseEntity<Object> setMemberNickName(@RequestBody @Valid SetMemBerNickNameRequest setMemBerNickNameRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK,groupPortInput.setMemberNickName(setMemBerNickNameRequest));
  }

  @GetMapping("/group/get-my-groups")
  public ResponseEntity<Object> getMyGroups(@ModelAttribute PaginationRequest paginationRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK , groupPortInput.getAllMyGroups(paginationRequest));
  }
}
