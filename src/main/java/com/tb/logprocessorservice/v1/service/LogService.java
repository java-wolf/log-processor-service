package com.tb.logprocessorservice.v1.service;

import com.tb.logprocessorservice.v1.mapper.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpStatus;
import com.tb.logprocessorservice.v1.model.LogEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LogService {

  @Value("${loggerator.host}")
  String hostname;

  @Value("${loggerator.port}")
  int port;

  @Autowired
  LogMapper logMapper;

  public List<String> processLogs(List<HttpStatus> codes, List<String> methods, List<String> users) throws UnknownHostException, IOException {
    try (Socket socket = new Socket(hostname, port)) {
      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      return reader
          .lines()
          .parallel()
          .map(x -> logMapper.toEntity(x))
          .filter(x -> x != null && applyFilters(x, codes, methods, users))
          .sorted((x,y) -> y.getDate().compareTo(x.getDate()))
          .map(logMapper::toLog)
          .collect(Collectors.toList());
      // return logs.stream().sorted((x,y) -> y.getDate().compareTo(x.getDate())).map(logMapper::toLog).collect(Collectors.toList());
    } catch (UnknownHostException ex) {
      System.err.println("Server not found: " + ex.getMessage());
      throw ex;
    } catch (IOException ex) {
      System.err.println("I/O error: " + ex.getMessage());
      throw ex;
    }
  }

  public boolean applyFilters(LogEntity logEntity, List<HttpStatus> codes, List<String> methods, List<String> users) {
    if (codes != null && codes.size() > 0 && !codes.contains(logEntity.getStatus())) {
      return false;
    }
    if (methods != null && methods.size() > 0 && !methods.stream().map(String::toUpperCase).anyMatch(m -> logEntity.getRequestMethod().contains(m))) {
      return false;
    }
    if (users != null && users.size() > 0 && !users.stream().anyMatch(u -> u.equalsIgnoreCase(logEntity.getAuthuser()))) {
      return false;
    }
    return true;
  }

}
