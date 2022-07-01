package pl.adamsiedlecki.obm.mongo.converter


import pl.adamsiedlecki.obm.config.ObmConfiguration
import pl.adamsiedlecki.obm.dto.BroadcastDto
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto
import pl.adamsiedlecki.obm.mongo.document.BroadcastDocument
import pl.adamsiedlecki.obm.mongo.document.MessageTypeEnum
import spock.lang.Specification

import java.time.LocalDateTime

class BroadcastConverterTest extends Specification {

    def config = new ObmConfiguration()
    def objectMapper = config.getObjectMapper() // it is not a mock

    def sut = new BroadcastConverter(objectMapper)

    def "should convert from dto to db object"() {
        given:
            def rssi = 23
            def text = "{aaa}"
            def dateTime = LocalDateTime.now()
            def messageTypeEnum = MessageTypeEnumDto.TEMPERATURE_REQUEST
            def broadcastDto = new BroadcastDto(rssi, text, dateTime, messageTypeEnum)

        when:
            def result = sut.convert(broadcastDto)

        then:
            result.rssi == rssi
            result.text == text
            result.dateTime == dateTime
            result.messageTypeEnum.name() == messageTypeEnum.name()
    }

    def "should convert from db object to dto"() {
        given:
            def rssi = 23
            def text = "{bbb}"
            def dateTime = LocalDateTime.now()
            def messageTypeEnum = MessageTypeEnum.UNKNOWN
            def broadcastDocument = new BroadcastDocument("any-id", rssi, text, dateTime, messageTypeEnum)

        when:
            def result = sut.convert(broadcastDocument)

        then:
            result.rssi() == rssi
            result.text() == text
            result.dateTime() == dateTime
            result.messageTypeEnum().name() == messageTypeEnum.name()
    }
}
