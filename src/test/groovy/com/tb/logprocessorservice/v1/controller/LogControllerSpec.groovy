package com.tb.logprocessorservice.v1.controller

import com.tb.logprocessorservice.v1.service.LogService
import org.springframework.http.HttpStatus;
import spock.lang.Specification;

class LogControllerSpec extends Specification {

  LogController logController
  LogService logServiceMock

  def setup() {
    logServiceMock = Mock()
    logController = new LogController(logService: logServiceMock)
  }

  def "log - No filter params"() {
    when:
    String[] result = logController.logs(null, null, null)

    then:
    1 * logServiceMock.processLogs(null, null, null)
    result == null
  }

  def "log - Filter params"() {
    given:
    List<Integer> codes = [200, 400]
    List<String> methods = ["methodA", "methodB"]
    List<String> users = ["userA", "userB"]

    when:
    String[] result = logController.logs(codes, methods, users)

    then:
    1 * logServiceMock.processLogs([HttpStatus.OK, HttpStatus.BAD_REQUEST], methods, users)
    result == null
  }

  def "log - Invalid code filter param"() {
    given:
    List<Integer> codes = [911, 400]
    List<String> methods = ["methodA", "methodB"]
    List<String> users = ["userA", "userB"]

    when:
    logController.logs(codes, methods, users)

    then:
    thrown IllegalArgumentException
  }

}
