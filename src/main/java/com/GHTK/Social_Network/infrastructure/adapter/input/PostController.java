package com.GHTK.Social_Network.infrastructure.adapter.input;

import com.GHTK.Social_Network.application.port.input.PostPortInput;
import com.GHTK.Social_Network.infrastructure.payload.requests.post.PostCreateRequest;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/post"})
@RequiredArgsConstructor
public class PostController {
    private final PostPortInput postPortInput;

    @PostMapping("/create-post")
    public ResponseEntity<Object> createPost(@RequestBody @Valid PostCreateRequest postCreateRequest){
        try {
            return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, postPortInput.createPost(postCreateRequest));
        } catch (Exception e) {
            return ResponseHandler.generateErrorResponse(e, HttpStatus.BAD_REQUEST);
        }
    }


}
