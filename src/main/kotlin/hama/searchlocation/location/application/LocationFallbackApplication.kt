package hama.searchlocation.location.application

import hama.searchlocation.location.domain.LocationFallbackRepository
import org.springframework.stereotype.Service

@Service
class LocationFallbackApplication(
    private val locationFallbackRepository: LocationFallbackRepository
) {
    fun save(keyword: String, locations: String) =
        locationFallbackRepository.save(keyword, locations)
}