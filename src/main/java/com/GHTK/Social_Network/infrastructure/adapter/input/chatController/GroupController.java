package com.GHTK.Social_Network.infrastructure.adapter.input.chatController;

import com.GHTK.Social_Network.application.port.input.chat.GroupPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.CreateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.requests.UpdateGroupRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.type.descriptor.java.ObjectJavaType;
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

  @PutMapping("/group/update")
  public ResponseEntity<Object> updateGroup(@RequestBody @Valid UpdateGroupRequest updateGroupRequest){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.updateGroup(updateGroupRequest));
  }

  @DeleteMapping("/group/delete")
  public  ResponseEntity<Object> deleteGroup(@RequestBody @Valid String groupId){
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, groupPortInput.deleteGroup(groupId));
  }


}
