package hama.searchlocation.location.infrastructure.kakao

import hama.searchlocation.location.domain.Location

// https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
data class KakaoResponse(
    val meta: Map<String, Any>,
    val documents: List<KakaoLocation>
) {
    fun toLocations(): List<Location> =
        documents.map { Location(it.place_name) }

    data class KakaoLocation(
        val place_name: String
    )
}
