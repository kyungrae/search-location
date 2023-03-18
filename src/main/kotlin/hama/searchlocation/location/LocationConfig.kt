package hama.searchlocation.location

import hama.searchlocation.location.domain.LocationQueryAggregator
import hama.searchlocation.location.infrastructure.kakao.KakaoLocationQueryClient
import hama.searchlocation.location.domain.LocationQueryClient
import hama.searchlocation.location.infrastructure.naver.NaverLocationQueryClient
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

    @Value("\${naver.client.id}")
    private lateinit var naverClientId: String

    @Value("\${naver.client.secret}")
    private lateinit var naverClientSecret: String

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

    @Bean
    fun naverRestTemplate(): RestTemplate =
        RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(1))
            .setReadTimeout(Duration.ofSeconds(1))
            .defaultHeader("X-Naver-Client-Id", naverClientId)
            .defaultHeader("X-Naver-Client-Secret", naverClientSecret)
            .rootUri("https://openapi.naver.com")
            .build()

    @Bean
    fun naverLocationQueryClient(): LocationQueryClient =
        NaverLocationQueryClient(naverRestTemplate())

    @Bean
    fun locationQueryAggregator(
    ) = LocationQueryAggregator(kakaoLocationQueryClient(), naverLocationQueryClient())
}