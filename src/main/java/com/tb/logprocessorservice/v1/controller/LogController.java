package com.tb.logprocessorservice.v1.controller;

import com.tb.logprocessorservice.v1.model.LogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import com.tb.logprocessorservice.v1.service.LogService;

@RestController
public class LogController {

  @Value("${loggerator.hostname}")
  String hostname;

  @Value("${loggerator.port}")
  int port;

  @Autowired
  LogService logService;

  @GetMapping("/logs")
  public List<LogEntity> logs(@RequestParam(value = "code", required = false) List<String> codes,
                     @RequestParam(value = "method", required = false) List<String> methods,
                     @RequestParam(value = "user", required = false) List<String> users) {
    List<HttpStatus> codesToFilter = null;
    try {
      codesToFilter = codes.stream().map(c -> HttpStatus.valueOf(Integer.valueOf(c))).collect(Collectors.toList());
    } catch(Exception e) {}
    return logService.processLogs(codesToFilter, methods, users);
  }
}
