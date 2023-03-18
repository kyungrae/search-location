package hama.searchlocation.location.config

import hama.searchlocation.location.domain.kakao.KakaoLocationQueryClient
import hama.searchlocation.location.domain.LocationQueryClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class LocationConfig {

    @Value("\${kakao.api.key}")
    private lateinit var kakaoApiKey: String

    @Bean
    fun kakaoRestTemplate(): RestTemplate =
        RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(1))
            .setReadTimeout(Duration.ofSeconds(1))
            .defaultHeader("Authorization", "KakaoAK $kakaoApiKey")
            .rootUri("https://dapi.kakao.com")
            .build()

    @Bean
    fun kakaoLocationQueryClient(): LocationQueryClient =
        KakaoLocationQueryClient(kakaoRestTemplate())
}