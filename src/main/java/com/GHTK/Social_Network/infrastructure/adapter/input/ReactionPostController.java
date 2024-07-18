package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.post.ReactionPostInput;
import com.GHTK.Social_Network.application.service.post.ReactionPostService;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.ReactionPostRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/reaction/posts")
@RequiredArgsConstructor
public class ReactionPostController {
    private final ReactionPostService  reactionPostService;

    @PostMapping("create")
    public ResponseEntity<Object> createReaction(@RequestBody @Valid ReactionPostRequest reactionPostRequest)throws MessagingException, UnsupportedEncodingException {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostService.createReactionPost(reactionPostRequest));
    }

    @PutMapping("update")
    public ResponseEntity<Object> updateReaction(@RequestBody @Valid ReactionPostRequest reactionPostRequest)throws MessagingException, UnsupportedEncodingException {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostService.updateReactionPost(reactionPostRequest));
    }
    @GetMapping("delete")
    public ResponseEntity<Object> DeleteReaction(@RequestBody @Valid Long id) throws MessagingException, UnsupportedEncodingException {
        return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, reactionPostService.deleteReactionPost(id));
    }
//    @GetMapping("{id}")
//    public
}
