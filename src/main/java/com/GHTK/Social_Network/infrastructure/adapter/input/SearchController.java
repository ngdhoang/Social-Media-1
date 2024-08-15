package com.GHTK.Social_Network.infrastructure.adapter.input;


import ai.djl.translate.TranslateException;
import ai.onnxruntime.OrtException;
import com.GHTK.Social_Network.application.port.input.SearchPortInput;
import com.GHTK.Social_Network.application.port.output.PhoBERTPortInput;
import com.GHTK.Social_Network.infrastructure.payload.responses.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/search"})
@RequiredArgsConstructor
public class SearchController {
  private final SearchPortInput searchPortInput;

  private final PhoBERTPortInput phoBERTPortInput;

  @GetMapping("")
  public ResponseEntity<Object> search(@RequestParam String q, @RequestParam(required = false) Integer s) {
    return ResponseHandler.generateResponse(ResponseHandler.MESSAGE_SUCCESS, HttpStatus.OK, searchPortInput.searchPublic(q, s));
  }

  @GetMapping("/infer")
  public String infer(@RequestParam String text) {
    try {

        boolean toxic = phoBERTPortInput.isToxic(text);

        return toxic ? "Toxic" : "Non-toxic";

    } catch (OrtException e) {
      throw new RuntimeException("Error during inference", e);
    } catch (TranslateException e) {
      throw new RuntimeException(e);
    }
  }
}
