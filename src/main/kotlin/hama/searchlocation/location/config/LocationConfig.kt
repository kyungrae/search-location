package hama.searchlocation.location.config

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
class LocationConfig(
    @Value("\${kakao.api.key:kakao.api.key}") private val kakaoApiKey: String,
    @Value("\${naver.client.id:naver.client.id}") private val naverClientId: String,
    @Value("\${naver.client.secret:naver.client.secret}") private val naverClientSecret: String
) {
    @Bean
    fun kakaoRestTemplate(): RestTemplate =
        RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(1))
            .setReadTimeout(Duration.ofSeconds(1))
            .defaultHeader("Authorization", "KakaoAK $kakaoApiKey")
            .rootUri(KAKAO_ROOT_URI)
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
            .rootUri(NAVER_ROOT_URI)
            .build()

    @Bean
    fun naverLocationQueryClient(): LocationQueryClient =
        NaverLocationQueryClient(naverRestTemplate())

    @Bean
    fun locationQueryAggregator(
    ) = LocationQueryAggregator(kakaoLocationQueryClient(), naverLocationQueryClient())

    companion object {
        const val KAKAO_ROOT_URI = "https://dapi.kakao.com"
        const val NAVER_ROOT_URI = "https://openapi.naver.com"
    }
}