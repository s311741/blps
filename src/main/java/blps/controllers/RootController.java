package blps.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// Just serve *something* on root
@RestController
@RequestMapping("/")
public class RootController {
  @RequestMapping
  private String serveRoot() {
    return "Hello from BLPS service!";
  }
}