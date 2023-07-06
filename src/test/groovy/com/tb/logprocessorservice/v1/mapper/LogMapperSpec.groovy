package com.tb.logprocessorservice.v1.mapper

import com.tb.logprocessorservice.v1.model.LogEntity
import org.springframework.http.HttpStatus
import spock.lang.Specification

import java.time.OffsetDateTime

class LogMapperSpec extends Specification {

    LogMapper logMapper

    def setup() {
        logMapper = new LogMapper()
    }

    def "toEntity - Filter params"() {
        given:
        String log = "23.59.50.157 - annstewart [09/Jul/2000 04:56:38 +0000] \"GET /followers/178 HTTP/1.0\" 200 505";

        when:
        LogEntity result = logMapper.toEntity(log)

        then:
        result
        result.remotehost == "23.59.50.157"
        result.rfc931 == "-"
        result.authuser == "annstewart"
        result.date == OffsetDateTime.parse("09/Jul/2000 04:56:38 +0000", LogMapper.formatter)
        result.requestMethod == "GET"
        result.request == "GET /followers/178 HTTP/1.0"
        result.status == HttpStatus.OK
        result.bytes == 505
    }

    def "toEntity - Invalid status code"() {
        given:
        String log = "23.59.50.157 - annstewart [09/Jul/2000 04:56:38 +0000] \"GET /followers/178 HTTP/1.0\" 911 505";

        when:
        logMapper.toEntity(log)

        then:
        thrown IllegalArgumentException
    }

    def "getLogDate"() {
        given:
        String log = "09/Jul/2000 04:56:38 +0000"

        when:
        OffsetDateTime result = logMapper.getLogDate(log)

        then:
        result
    }

    def "getLogDateText"() {
        given:
        OffsetDateTime date = OffsetDateTime.parse("09/Jul/2000 04:56:38 +0000", LogMapper.formatter)

        when:
        String result = logMapper.getLogDateText(date)

        then:
        result == "9/Jul/2000 04:56:38 +0000"
    }

    def "toLog"() {
        given:
        LogEntity logEntity = new LogEntity()
        logEntity.remotehost = "23.59.50.157"
        logEntity.rfc931 = "-"
        logEntity.authuser == "annstewart"
        logEntity.date = OffsetDateTime.parse("09/Jul/2000 04:56:38 +0000", LogMapper.formatter)
        logEntity.request = "GET /followers/178 HTTP/1.0"
        logEntity.status = HttpStatus.OK
        logEntity.bytes = 505

        when:
        String result = logMapper.toLog(logEntity)

        then:
        result == "23.59.50.157 - null [9/Jul/2000 04:56:38 +0000] \"GET /followers/178 HTTP/1.0\" 200 505"
    }

}
