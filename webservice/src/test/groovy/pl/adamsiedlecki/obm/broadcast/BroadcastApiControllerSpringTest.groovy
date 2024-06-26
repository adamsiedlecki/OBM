package pl.adamsiedlecki.obm.broadcast

import org.openapitools.model.BroadcastListOutput
import org.spockframework.spring.SpringSpy
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import pl.adamsiedlecki.obm.dto.MessageTypeEnumDto
import pl.adamsiedlecki.obm.facade.BroadcastDbFacade
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BroadcastApiControllerSpringTest extends Specification {

    @LocalServerPort
    int randomServerPort

    @SpringSpy
    BroadcastDbFacade broadcastDbFacade

    WebTestClient webTestClient
    static final MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:4.0.10")

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDbContainer::getReplicaSetUrl)
    }

    void setupSpec() {
        mongoDbContainer.start()
    }

    void setup() {
        webTestClient = WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + randomServerPort + "/api/v1/broadcasts")
                .build()
        broadcastDbFacade.deleteAll()
    }

    def "should process request correctly and save broadcast to database as unknown"() {
        given:
            def textBase64 = Base64.getEncoder().encodeToString("radio message".getBytes())
            def requestJson = '{"rssi":67, "text":"' + textBase64 + '"}'

        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()
            sleepSeconds(1)

            def dbResults = broadcastDbFacade.findAll()
            LocalDateTime now = LocalDateTime.now()

            dbResults.size() == 1
            dbResults[0].text() == "radio message"
            dbResults[0].rssi() == 67
            dbResults[0].messageTypeEnum() == MessageTypeEnumDto.UNKNOWN
            dbResults[0].dateTime().isBefore(now)
            dbResults[0].dateTime().isAfter(now.minusMinutes(5))
    }

    def "should process request correctly and save broadcast to database as temperature request"() {
        given:
            def text = '{"tid":4,"cmm":"tR"}'
            def textBase64 = Base64.getEncoder().encodeToString(text.getBytes())
            def requestJson = '{"rssi":67, "text":"' + textBase64 + '"}'


        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()
            sleepSeconds(1)

            def dbResults = broadcastDbFacade.findAll()
            LocalDateTime now = LocalDateTime.now()

            dbResults.size() == 1
            dbResults[0].text() == text
            dbResults[0].rssi() == 67
            dbResults[0].messageTypeEnum() == MessageTypeEnumDto.TEMPERATURE_REQUEST
            dbResults[0].dateTime().isBefore(now)
            dbResults[0].dateTime().isAfter(now.minusMinutes(5))
    }

    def "should process request and put text parsed correctly"() {
        given:
            def text = '{"tid":4,"cmm":"tR", "object": {"cat": ["meeeow", "meow"]}}'
            def textBase64 = Base64.getEncoder().encodeToString(text.getBytes())
            def requestJson = '{"rssi":67, "text":"' + textBase64 + '"}'


        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()
            sleepSeconds(1)

            def dbResults = broadcastDbFacade.findAll()
            LocalDateTime now = LocalDateTime.now()

            dbResults.size() == 1
            dbResults[0].text() == text
            dbResults[0].rssi() == 67
            dbResults[0].messageTypeEnum() == MessageTypeEnumDto.UNKNOWN
            dbResults[0].dateTime().isBefore(now)
            dbResults[0].dateTime().isAfter(now.minusMinutes(5))
            dbResults[0].textParsed().get("tid") == 4
            (dbResults[0].textParsed().get("object") as Map).get("cat")[1] == "meow"
    }

    def "should return 400 because of lacking text property in body"() {
        given:
            def requestJson = '{"rssi":67}'

        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is4xxClientError()
            sleepSeconds(1)
            broadcastDbFacade.count() == 0
    }

    def "should return 200 in case of database error because of async"() {
        given:
            def text = '{"tid":4,"cmm":"tR"}'
            def textBase64 = Base64.getEncoder().encodeToString(text.getBytes())
            def requestJson = '{"rssi":67, "text":"' + textBase64 + '"}'
            1 * broadcastDbFacade.save(_) >> {throw new RuntimeException("sth went wrong ;)")}

        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()
            sleepSeconds(1)
            broadcastDbFacade.count() == 0
    }

    def "should return broadcasts from database"() {
        given:
        def textBase64 = Base64.getEncoder().encodeToString("radio message".getBytes())
        def requestJson = '{"rssi":67, "text":"' + textBase64 + '"}'

        webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()
        webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()
        webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()

        expect:
        sleepSeconds(1)
        def result = webTestClient.get().exchange().expectStatus().is2xxSuccessful().expectBody(BroadcastListOutput).returnResult()
        result.getResponseBody().bList.size() == 3
    }

    def sleepSeconds(int seconds) {
        Thread.sleep(seconds * 1000)
        return true
    }
}
