package hama.searchlocation.location.domain.naver

import hama.searchlocation.location.domain.Location

data class NaverResponse(
    val items: List<NaverLocation>
) {
    fun toLocations(): List<Location> =
        items.map { Location(it.title) }

    data class NaverLocation(
        val title: String
    )
}
