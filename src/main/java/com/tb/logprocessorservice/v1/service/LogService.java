package com.tb.logprocessorservice.v1.service;

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
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogService {
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy kk:mm:ss Z", Locale.US);
  // Log Input format:
  // 23.59.50.157 - annstewart [09/Jul/2000 04:56:38 +0000] "GET /followers/178 HTTP/1.0" 200 505
  private static String LOG_INPUT_REGEX = "^(\\S+) (\\S+) (\\S+) \\[(.+)\\] \"(.+)\" (\\S+) (\\S+)$";

  @Value("${loggerator.hostname}")
  String hostname;

  @Value("${loggerator.port}")
  int port;

  public List<LogEntity> processLogs(List<HttpStatus> codes, List<String> methods, List<String> users) {
    List<LogEntity> logs = new ArrayList<>();
    LogEntity logEntity;
    String sData;
    try (Socket socket = new Socket(hostname, port)) {
      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      do {
        sData = reader.readLine();
        logEntity = sData != null ? processLog(sData, codes, methods, users) : null;
        if (logEntity != null) {
          logs.add(logEntity);
        }
      } while (sData != null);
    } catch (UnknownHostException ex) {
      System.err.println("Server not found: " + ex.getMessage());
    } catch (IOException ex) {
      System.err.println("I/O error: " + ex);
    }
    return logs;
  }

  public LogEntity processLog(String data, List<HttpStatus> codes, List<String> methods, List<String> users) {
    if (data != null && data.length() > 0) {
      LogEntity logEntity = mapToEntity(data);
      if(applyFilters(logEntity, codes, methods, users)) {
        return logEntity;
      };
    }
    return null;
  }

  public boolean applyFilters(LogEntity logEntity, List<HttpStatus> codes, List<String> methods, List<String> users) {
    if (codes != null && codes.size() > 0 && !codes.contains(logEntity.getStatus())) {
      return false;
    }
    if (methods != null && methods.size() > 0 && !methods.stream().map(String::toUpperCase).anyMatch(m -> logEntity.getRequest().contains(m))) {
      return false;
    }
    if (users != null && users.size() > 0 && !users.stream().anyMatch(u -> u.equalsIgnoreCase(logEntity.getAuthuser()))) {
      return false;
    }
    return true;
  }

  public LogEntity mapToEntity(String data) {
    LogEntity logEntity = new LogEntity();
    if (data != null && data.length() > 0) {
      Pattern pattern = Pattern.compile(LOG_INPUT_REGEX);
      Matcher matcher = pattern.matcher(data);
      if (matcher.matches()) {
        logEntity.setRemotehost(matcher.group(1));
        logEntity.setRfc931(matcher.group(2));
        logEntity.setAuthuser(matcher.group(3));
        logEntity.setDate(getLogDate(matcher.group(4)));
        logEntity.setRequest(matcher.group(5));
        logEntity.setStatus(HttpStatus.valueOf(Integer.valueOf(matcher.group(6))));
        logEntity.setBytes(Integer.valueOf(matcher.group(7)));
      }
    }
    return logEntity;
  }

  LocalDateTime getLogDate(String logText) {
    return LocalDateTime.parse(logText, formatter);
  }

}
