package com.tb.logprocessorservice.v1.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

import com.tb.logprocessorservice.v1.service.LogService;

@RestController
public class LogController {

  @Value("${loggerator.host}")
  String hostname;

  @Value("${loggerator.port}")
  int port;

  @Autowired
  LogService logService;

  @ApiOperation(value = "Returns system access logs",
      notes = "Returns system access logs provided by the Loggerator in descending order by date.<br>" +
          "Access logs can be filtered by Http response codes, request methods, and usernames.")
  @GetMapping("/logs")
  public List<String> logs(
      @ApiParam(value = "Filter by Http Response Codes", example = "200,500") @RequestParam(value = "code", required = false) List<Integer> codes,
      @ApiParam(value = "Filter by Http Request Methods", example = "GET,POST,PUT") @RequestParam(value = "method", required = false) List<String> methods,
      @ApiParam(value = "Filter by users", example = "brucebarnes,jenniferdiaz") @RequestParam(value = "user", required = false) List<String> users) throws UnknownHostException, IOException  {
    List<HttpStatus> codesToFilter = null;
    if(codes != null && codes.size() > 0) {
      try {
        codesToFilter = codes.stream().map(c -> HttpStatus.valueOf(Integer.valueOf(c))).collect(Collectors.toList());
      } catch (Exception e) {
        System.err.println("Error parsing Http codes: " + e);
        throw e;
      }
    }
    return logService.processLogs(codesToFilter, methods, users);
  }
}
