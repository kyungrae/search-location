package hama.searchlocation.location.domain.kakao

import hama.searchlocation.location.domain.Location

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
