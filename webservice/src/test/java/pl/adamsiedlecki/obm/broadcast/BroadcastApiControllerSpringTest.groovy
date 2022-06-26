package pl.adamsiedlecki.obm.broadcast

import org.apache.tomcat.jni.Local
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.MongoDBContainer
import pl.adamsiedlecki.obm.mongo.document.MessageTypeEnum
import pl.adamsiedlecki.obm.mongo.repo.BroadcastDocumentRepo
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BroadcastApiControllerSpringTest extends Specification {

    @LocalServerPort
    int randomServerPort

    @Autowired
    BroadcastDocumentRepo broadcastDocumentRepo

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
        broadcastDocumentRepo.deleteAll()
    }

    def "should process request correctly and save broadcast to database"() {
        given:
            def requestJson = '{"rssi":67, "text":"radio message"}'

        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is2xxSuccessful()

            def dbResults = broadcastDocumentRepo.findAll()
            LocalDateTime now = LocalDateTime.now()

            dbResults.size() == 1
            dbResults[0].text == "radio message"
            dbResults[0].rssi == 67
            dbResults[0].messageTypeEnum == MessageTypeEnum.UNKNOWN
            dbResults[0].dateTime.isBefore(now)
            dbResults[0].dateTime.isAfter(now.minusMinutes(5))
    }

    def "should return 400 because of lacking text property in body"() {
        given:
            def requestJson = '{"rssi":67}'

        expect:
            webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is4xxClientError()
            broadcastDocumentRepo.count() == 0
    }

    def "should return 500 because of database error"() {
        given:
        def requestJson = '{"rssi":67, "text":"radio message"}'
        1 * broadcastDocumentRepo.save(_) >> {throw new RuntimeException("sth went wrong ;)")}

        expect:
        webTestClient.post().contentType(MediaType.APPLICATION_JSON).bodyValue(requestJson).exchange().expectStatus().is5xxServerError()
        broadcastDocumentRepo.count() == 0
    }
}