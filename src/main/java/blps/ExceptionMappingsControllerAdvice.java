package blps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionMappingsControllerAdvice {
  private static final Logger log = LoggerFactory.getLogger(ExceptionMappingsControllerAdvice.class);

  @ExceptionHandler(NoSuchElementException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  private void notFound(NoSuchElementException e) {
    log.info("Not found: {}", e.getMessage());
  }
}
