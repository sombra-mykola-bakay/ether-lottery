package com.sombrainc.ether.lottery.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger("ERROR");

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleException(HttpServletRequest request,
      MethodArgumentNotValidException ex) {
    LOGGER.error(ex.getLocalizedMessage(), ex);
    String message = ex.getBindingResult().getAllErrors().stream().map(
        DefaultMessageSourceResolvable::getDefaultMessage)
        .reduce((a, b) -> a + ". " + b).orElse(null);

    Map<String, Object> errorAttributes = new LinkedHashMap<>();
    errorAttributes.put("timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
    errorAttributes.put("status", HttpStatus.BAD_REQUEST.value());
    errorAttributes.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
    errorAttributes.put("message", message);
    errorAttributes.put("path", request.getServletPath());

    return new ResponseEntity<>(errorAttributes, HttpStatus.BAD_REQUEST);
  }

}