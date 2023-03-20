package hama.searchlocation.location

import hama.searchlocation.location.config.LocationConfig.Companion.KAKAO_ROOT_URI
import hama.searchlocation.location.config.LocationConfig.Companion.NAVER_ROOT_URI
import hama.searchlocation.location.config.LocationKafkaConfig.Companion.LOCATION_SEARCH_TOPIC
import hama.searchlocation.location.infrastructure.kakao.KakaoLocationQueryClient
import hama.searchlocation.location.infrastructure.naver.NaverLocationQueryClient
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.MediaType
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.TopicPartitionOffset
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.web.client.ExpectedCount.manyTimes
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = [LOCATION_SEARCH_TOPIC], bootstrapServersProperty = "spring.kafka.bootstrap-servers")
internal class LocationIntegrationTest(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val locationSearchKafkaTemplate: KafkaTemplate<String, String>,
    @Autowired val kafkaProperties: KafkaProperties,
    @Autowired val kakaoRestTemplate: RestTemplate,
    @Autowired val naverRestTemplate: RestTemplate
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
        val keyword = "가"

        // Given 1. kakao mock server 설정
        val kakaoMockServer = MockRestServiceServer.createServer(kakaoRestTemplate)
        val kakaoURI = UriComponentsBuilder.fromUriString(KAKAO_ROOT_URI + KakaoLocationQueryClient.PATH)
            .queryParam("query", keyword).queryParam("page", 1).queryParam("size", 5)
            .build().encode().toUri() // 곰바위
        val kakaoResponse = "{\"documents\":[{\"place_name\":\"A\"}, {\"place_name\":\"B\"}, {\"place_name\":\"C\"}, {\"place_name\":\"D\"}, {\"place_name\":\"E\"}], \"meta\":{}}"
        kakaoMockServer
            .expect(manyTimes(), requestTo(kakaoURI))
            .andRespond(withSuccess(kakaoResponse, MediaType.APPLICATION_JSON))

        // Given 2. Naver mock server 설정
        val naverMockServer = MockRestServiceServer.createServer(naverRestTemplate)
        val naverURI = UriComponentsBuilder.fromUriString(NAVER_ROOT_URI + NaverLocationQueryClient.PATH)
            .queryParam("query", keyword).queryParam("start", 1).queryParam("display", 5)
            .build().encode().toUri()
        val naverResponse = "{\"items\":[{\"title\":\"A\"}, {\"title\":\"B\"}, {\"title\":\"D\"}, {\"title\":\"E\"}, {\"title\":\"F\"}]}"
        naverMockServer
            .expect(manyTimes(), requestTo(naverURI))
            .andRespond(withSuccess(naverResponse, MediaType.APPLICATION_JSON))

        // when
        val response = restTemplate.getForEntity("/v1/locations?keyword=$keyword", List::class.java)
        val responseBody = requireNotNull(response.body)

        // then normal response
        assertTrue(response.statusCode.is2xxSuccessful)
        assertNotNull(response.body)
        assertEquals(6, responseBody.size)
        assertIterableEquals(setOf("A", "B", "D", "E"), responseBody.subList(0, 4))
        assertEquals("C", responseBody[4])
        assertEquals("F", responseBody.last())

        // then produce kafka record
        locationSearchKafkaTemplate.setConsumerFactory(DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties()))
        val partitions = listOf(TopicPartitionOffset("location.search.success", 1, 0))
        val record = locationSearchKafkaTemplate.receive(partitions).last()
        assertNotNull(record)
    }
}