package hama.searchlocation.location.application

import hama.searchlocation.location.domain.LocationQueryAggregator
import org.springframework.stereotype.Service

@Service
class LocationApplication(
    private val locationQueryAggregator: LocationQueryAggregator
) {
    fun getLocations(keyword: String): List<String> {
        return locationQueryAggregator.query(keyword)
    }
}