package com.tb.logprocessorservice.v1.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.http.HttpStatus;

@Data
public class LogEntity {
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy kk:mm:ss Z");

  private String rawData;

  private String remotehost;
  private String rfc931;
  private String authuser;
  private LocalDateTime date;
  private String request;
  private HttpStatus status;
  private Integer bytes;

//  public String toString() {
//    return rawData;
//  }
  public String toString() {
    return remotehost + ' '
        +rfc931+ ' '
        +authuser+' '
        +'['+getLogDateText(date)+']'+' '
        +'"'+request+'"'+' '
        +status.value()+' '
        +bytes;
  }

  String getLogDateText(LocalDateTime logDate) {
    try {
      return logDate.format(formatter);
    } catch(Exception e) {
      return "";
    }
    // return logDate.format(formatter);
  }
}
