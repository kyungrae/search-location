package hama.searchlocation.location.domain.kakao

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class KakaoResponse(
    val meta: Map<String, Any>,
    val documents: List<KakaoLocation>
) {
    val locations: List<String>
        get() = documents.map { it.placeName }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class KakaoLocation(
        val id: String,
        val placeName: String,
        val categoryName: String,
        val categoryGroup_code: String,
        val categoryGroup_name: String,
        val phone: String,
        val addressName: String,
        val roadAddressName: String,
        val x: String,
        val y: String,
        val place_url: String,
        val distance: String
    )
}
