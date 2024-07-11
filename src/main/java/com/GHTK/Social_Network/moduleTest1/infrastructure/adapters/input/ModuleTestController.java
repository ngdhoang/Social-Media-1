package com.GHTK.Social_Network.moduleTest1.infrastructure.adapters.input;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ModuleTestController {
  @GetMapping
  public String test() {
    return "test (Huong Dep Trai)";
  }
}
