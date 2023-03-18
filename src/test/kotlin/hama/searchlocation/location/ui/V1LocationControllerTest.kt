package hama.searchlocation.location.ui

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class V1LocationControllerTest(
    @Autowired val restTemplate: TestRestTemplate
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
    fun when_keyword_is_food__return_normal() {
        // given
        val keyword = "곱창"

        // when
        val response = restTemplate.getForEntity("/v1/locations?keyword=$keyword", String::class.java)

        println(response)

        // then
        assertTrue(response.statusCode.is2xxSuccessful)
        assertFalse(response.body.isNullOrEmpty())
    }
}