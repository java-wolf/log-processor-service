package com.tb.logprocessorservice.v1.model;

import lombok.Data;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

@Data
public class LogEntity {
  private String remotehost;
  private String rfc931;
  private String authuser;
  private OffsetDateTime date;
  private String request;
  private String requestMethod;
  private HttpStatus status;
  private Integer bytes;
}
