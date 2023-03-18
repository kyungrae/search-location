package hama.searchlocation.location.domain.naver

import hama.searchlocation.location.domain.Location
import hama.searchlocation.location.domain.LocationQueryClient
import org.springframework.web.client.RestTemplate

// https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
class NaverLocationQueryClient(
    private val kakaoRestTemplate: RestTemplate
) : LocationQueryClient {
    override fun getLocations(keyword: String): List<Location> =
        kakaoRestTemplate.getForEntity(
            "/v1/search/local.json?query=곱창&display=5",
            NaverResponse::class.java
        ).body!!.toLocations()

}