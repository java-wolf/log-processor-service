package com.tb.logprocessorservice.v1.model;

import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

@Data
public class LogEntity {
  private String remotehost;
  private String rfc931;
  private String authuser;
  private LocalDateTime date;
  private String request;
  private HttpStatus status;
  private Integer bytes;
}
