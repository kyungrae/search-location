package hama.searchlocation.location.infrastructure.kakao

import hama.searchlocation.location.domain.Location
import hama.searchlocation.location.domain.LocationQueryClient
import org.springframework.web.client.RestTemplate

// https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
class KakaoLocationQueryClient(
    private val kakaoRestTemplate: RestTemplate
) : LocationQueryClient {
    override fun getLocations(keyword: String, page: Int, size: Int): List<Location> =
        kakaoRestTemplate.getForEntity(
            "$PATH?query=$keyword&page=$page&size=$size",
            KakaoResponse::class.java
        ).body!!.toLocations()

    companion object {
        const val PATH = "/v2/local/search/keyword.json"
    }
}