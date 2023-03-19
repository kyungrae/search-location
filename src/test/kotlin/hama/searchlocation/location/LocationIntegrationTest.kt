package hama.searchlocation.location

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.TopicPartitionOffset
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = ["location.search.success"], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
internal class LocationIntegrationTest(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val locationSearchKafkaTemplate: KafkaTemplate<String, String>,
    @Autowired val kafkaProperties: KafkaProperties
) {
    @Test
    fun badRequestTest() {
        // given
        val keyword = ""

        // when
        val response = restTemplate.getForEntity("/v1/locations?keyword=$keyword", String::class.java)

        // then
        assertTrue(response.statusCode.is4xxClientError)
    }

    @Test
    fun normalRequestTest() {
        // given
        val keyword = "곱창"

        // when
        val response = restTemplate.getForEntity("/v1/locations?keyword=$keyword", String::class.java)

        // then normal response
        assertTrue(response.statusCode.is2xxSuccessful)
        assertFalse(response.body.isNullOrEmpty())

        // then produce kafka record
        locationSearchKafkaTemplate.setConsumerFactory(DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties()))
        val partitions = listOf(TopicPartitionOffset("location.search.success", 1, 0))
        val record = locationSearchKafkaTemplate.receive(partitions).last()
        assertNotNull(record)
    }
}