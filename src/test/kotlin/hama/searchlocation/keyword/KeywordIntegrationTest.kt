package hama.searchlocation.keyword

import hama.searchlocation.keyword.domain.KeywordSearchLog
import hama.searchlocation.keyword.ui.KeywordConsumer.Companion.KEYWORD_ACCUMULATOR_CONTAINER_ID
import hama.searchlocation.location.config.LocationKafkaConfig.Companion.LOCATION_SEARCH_TOPIC
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.lang.Thread.sleep


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = [LOCATION_SEARCH_TOPIC], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@Testcontainers
internal class KeywordIntegrationTest(
    @Autowired private val jdbcTemplate: JdbcTemplate,
    @Autowired private val locationKafkaTemplate: KafkaTemplate<String, String>,
    @Autowired private val registry: KafkaListenerEndpointRegistry,
    @Autowired private val restTemplate: TestRestTemplate
) {
    @Test
    fun when_location_search_success_event_receive__keyword_search_logs_are_accumulated() {
        // Given 1. keyword search log 데이터 테이블 적재를 위해 container 실행
        val listenerContainer = requireNotNull(registry.getListenerContainer(KEYWORD_ACCUMULATOR_CONTAINER_ID))
        listenerContainer.start()

        // Given 2. 기존에 있는 keyword search log
        jdbcTemplate.execute("INSERT INTO keyword_search_log (keyword,search_count) VALUES ('hama',100),('dragon',10),('snake',5),('horse',4),('sheep',3), ('monkey',2),('chicken',1)")

        // Given 3. location search event 토픽에 적재
        val mouses = List(10) { "mouse" }
        val cows = List(9) { "cow" }
        val tigers = List(8) { "tiger" }
        val rabbits = List(7) { "rabbit" }
        val dragons = List(6) { "dragon" }
        val hamas = List(5) { "hama" }
        val keywords = (hamas + mouses + cows + tigers + rabbits + dragons).shuffled()
        keywords.forEach { locationKafkaTemplate.sendDefault(it, it) }

        // container가 메세지 처리 하기 위해 sleep
        sleep(1000)

        // when
        val response = restTemplate.restTemplate.exchange(
            "/v1/keywords/popular",
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<KeywordSearchLog>>() {})
        val responseBody = requireNotNull(response.body)

        // then
        val expected = listOf(
            KeywordSearchLog("hama", 105),
            KeywordSearchLog("dragon", 16),
            KeywordSearchLog("mouse", 10),
            KeywordSearchLog("cow", 9),
            KeywordSearchLog("tiger", 8),
            KeywordSearchLog("rabbit", 7),
            KeywordSearchLog("snake", 5),
            KeywordSearchLog("horse", 4),
            KeywordSearchLog("sheep", 3),
            KeywordSearchLog("monkey", 2)
        )
        assertTrue(response.statusCode.is2xxSuccessful)
        assertIterableEquals(expected.map { it.keyword }, responseBody.map { it.keyword })
        assertIterableEquals(expected.map { it.searchCount }, responseBody.map { it.searchCount })

        listenerContainer.stop()
    }

    companion object {
        @JvmStatic
        val mySQLContainer: MySQLContainer<*> = MySQLContainer("mysql:8.0.32")
            .apply { start() }
    }
}