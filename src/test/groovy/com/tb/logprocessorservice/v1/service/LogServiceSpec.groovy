package com.tb.logprocessorservice.v1.service

import com.tb.logprocessorservice.v1.mapper.LogMapper
import com.tb.logprocessorservice.v1.model.LogEntity
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.time.OffsetDateTime

class LogServiceSpec extends Specification {

  LogService logService
  LogMapper logMapperMock
  LogEntity logEntity

  def setup() {
    logMapperMock = Mock()
    logService = new LogService(logMapper: logMapperMock)

    logEntity = new LogEntity()
    logEntity.remotehost = "23.59.50.157"
    logEntity.rfc931 = "-"
    logEntity.authuser = "annstewart"
    logEntity.date = OffsetDateTime.parse("09/Jul/2000 04:56:38 +0000", LogMapper.formatter)
    logEntity.requestMethod = "GET"
    logEntity.request = "GET /followers/178 HTTP/1.0"
    logEntity.status = HttpStatus.OK
    logEntity.bytes = 505
  }

//  def "applyFilters"() {
//    given:
//    List<HttpStatus> codes = [HttpStatus.OK]
//    List<String> methods = ["GET", "POST"]
//    List<String> users = ["userA", "annstewart"]
//
//    when:
//    boolean result = logService.applyFilters(logEntity, codes, methods, users)
//
//    then:
//    result
//  }
//
//  def "!applyFilters"() {
//    given:
//    List<HttpStatus> codes = [HttpStatus.BAD_REQUEST]
//    List<String> methods = ["GET", "POST"]
//    List<String> users = ["userA", "annstewart"]
//
//    when:
//    boolean result = logService.applyFilters(logEntity, codes, methods, users)
//
//    then:
//    !result
//  }

//  def "applyFilters 2"(List<HttpStatus> codes, List<String> methods, List<String> users, Boolean expected) {
//    given:
//    List<HttpStatus> a = codes.collect{ it }
//    List<String> b = methods.collect{ it }
//    List<String> c = users.collect{ it }
//
//    expect:
//    assert expected == logService.applyFilters(logEntity, a, b, c)
//
//    where:
//    codes | methods | users || expected
//    [HttpStatus.OK] | ["GET", "POST"] | ["userA", "annstewart"] || Boolean.TRUE
////    [HttpStatus.BAD_GATEWAY] | ["GET", "POST"] | ["userA", "annstewart"] | true
////    [HttpStatus.OK] | ["GET", "POST"] | ["userA", "annstewart"] | true
////    [HttpStatus.OK] | ["GET", "POST"] | ["userA", "annstewart"] | true
//  }

  def "applyFilters 2"(HttpStatus codes, String methods, String users, Boolean expected) {
    given:
    //List<HttpStatus> a = codes.collect{ it }
    //List<String> b = methods.collect{ it }
    //List<String> c = users.collect{ it }

    expect:
    assert expected == logService.applyFilters(logEntity, [codes], [methods], [users])

    where:
    codes | methods | users || expected
    HttpStatus.OK | "GET" | "annstewart" || Boolean.TRUE
//    [HttpStatus.BAD_GATEWAY] | ["GET", "POST"] | ["userA", "annstewart"] | true
//    [HttpStatus.OK] | ["GET", "POST"] | ["userA", "annstewart"] | true
//    [HttpStatus.OK] | ["GET", "POST"] | ["userA", "annstewart"] | true
  }

  /*
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
   */


}
