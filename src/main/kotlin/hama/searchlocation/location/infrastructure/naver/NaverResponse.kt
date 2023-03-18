package hama.searchlocation.location.infrastructure.naver

import hama.searchlocation.location.domain.Location

// https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
data class NaverResponse(
    val items: List<NaverLocation>
) {
    fun toLocations(): List<Location> =
        items.map { Location(it.title) }

    data class NaverLocation(
        val title: String
    )
}
