package hama.searchlocation.location.application

import hama.searchlocation.location.domain.LocationQueryClient
import org.springframework.stereotype.Service

@Service
class LocationApplication(
    private val kakaoLocationQueryClient: LocationQueryClient,
    private val naverLocationQueryClient: LocationQueryClient
) {

    fun getLocations(keyword: String): List<String> {
        return kakaoLocationQueryClient.getLocations(keyword) + naverLocationQueryClient.getLocations(keyword)
    }
}