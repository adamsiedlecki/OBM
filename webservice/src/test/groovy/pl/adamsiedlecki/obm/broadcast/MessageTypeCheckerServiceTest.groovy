package pl.adamsiedlecki.obm.broadcast


import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto
import pl.adamsiedlecki.obm.test.utils.TestConfiguration
import spock.lang.Specification

class MessageTypeCheckerServiceTest extends Specification {

    def mapper = TestConfiguration.obmConfiguration().objectMapper

    MessageTypeCheckerService sut = new MessageTypeCheckerService(mapper)

    def "should state that this is temperature request"() {
        expect:
            sut.check('{"tid":4,"cmm":"tR"}') == MessageTypeEnumDto.TEMPERATURE_REQUEST
    }

    def "should state that this is temperature response"() {
        expect:
            sut.check('{a:4,tp:34.12}') == MessageTypeEnumDto.TEMPERATURE_RESPONSE
    }

    def "should state that this is voltage request"() {
        expect:
            sut.check('{"tid":4,"cmm":"vR"}') == MessageTypeEnumDto.VOLTAGE_REQUEST
    }

    def "should state that this is voltage response"() {
        expect:
            sut.check('{a:4,v:12.10}') == MessageTypeEnumDto.VOLTAGE_RESPONSE
    }

    def "should state that this is humidity request"() {
        expect:
            sut.check('{tid:2, cmm:"hR"}') == MessageTypeEnumDto.HUMIDITY_REQUEST
    }

    def "should state that this is humidity response"() {
        expect:
            sut.check('{a:2,hu:60.63}') == MessageTypeEnumDto.HUMIDITY_RESPONSE
    }

    def "should state that this is gps broadcast"() {
        expect:
        sut.check('{a:10,lat:51.8473871,lng:20.9270731}') == MessageTypeEnumDto.GPS_BROADCAST
    }

    def "should state that text has unknown schema"() {
        expect:
        sut.check('whatever') == MessageTypeEnumDto.UNKNOWN
    }

}
