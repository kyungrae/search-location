package hama.searchlocation.location.infrastructure.naver

import hama.searchlocation.location.domain.Location
import hama.searchlocation.location.domain.LocationQueryAggregator.Companion.QUERY_SIZE
import hama.searchlocation.location.domain.LocationQueryClient
import org.springframework.web.client.RestTemplate

// https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
class NaverLocationQueryClient(
    private val kakaoRestTemplate: RestTemplate
) : LocationQueryClient {
    override fun getLocations(keyword: String, page: Int, size: Int): List<Location> =
        kakaoRestTemplate.getForEntity(
            "$PATH?query=$keyword&start=${(page - 1) * QUERY_SIZE + 1}&display=$size",
            NaverResponse::class.java
        ).body!!.toLocations()

    companion object {
        const val PATH = "/v1/search/local.json"
    }
}