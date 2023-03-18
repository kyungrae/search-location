package hama.searchlocation.location.domain.kakao

import hama.searchlocation.location.domain.LocationQueryClient

import org.springframework.web.client.RestTemplate

class KakaoLocationQueryClient(
    private val kakaoRestTemplate: RestTemplate
) : LocationQueryClient {

    override fun getLocations(keyword: String): List<String> =
        kakaoRestTemplate.getForEntity(
            "/v2/local/search/keyword.json?query=$keyword&page=1&size=10",
            KakaoResponse::class.java
        ).body!!.locations

}