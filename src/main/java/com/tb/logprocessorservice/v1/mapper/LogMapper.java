package com.tb.logprocessorservice.v1.mapper;

import com.tb.logprocessorservice.v1.model.LogEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LogMapper {
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MMM/yyyy kk:mm:ss Z");
  // Log Input format:
  // 23.59.50.157 - annstewart [09/Jul/2000 04:56:38 +0000] "GET /followers/178 HTTP/1.0" 200 505
  private static String LOG_INPUT_REGEX = "^(\\S+) (\\S+) (\\S+) \\[(.+)\\] \"(.+)\" (\\S+) (\\S+)$";

  public LogEntity toEntity(String log) {
    LogEntity logEntity = null;
    if (log != null && log.length() > 0) {
      Pattern pattern = Pattern.compile(LOG_INPUT_REGEX);
      Matcher matcher = pattern.matcher(log);
      if (matcher.matches()) {
        logEntity = new LogEntity();
        logEntity.setRemotehost(matcher.group(1));
        logEntity.setRfc931(matcher.group(2));
        logEntity.setAuthuser(matcher.group(3));
        logEntity.setDate(getLogDate(matcher.group(4)));
        String request = matcher.group(5);
        logEntity.setRequest(request);
        String requestMethod = request == null ? null : request.split(" ")[0];
        logEntity.setRequestMethod(requestMethod);
        logEntity.setStatus(HttpStatus.valueOf(Integer.valueOf(matcher.group(6))));
        logEntity.setBytes(Integer.valueOf(matcher.group(7)));
      }
    }
    return logEntity;
  }

  public String toLog(LogEntity entity) {
    return entity == null ? null :
        entity.getRemotehost() + ' '
        +entity.getRfc931()+ ' '
        +entity.getAuthuser()+' '
        +'['+getLogDateText(entity.getDate())+']'+' '
        +'"'+entity.getRequest()+'"'+' '
        +entity.getStatus().value()+' '
        +entity.getBytes();
  }

  OffsetDateTime getLogDate(String logDateText) {
    return OffsetDateTime.parse(logDateText, formatter);
  }

  String getLogDateText(OffsetDateTime logDate) {
    return logDate.format(formatter);
  }

}
