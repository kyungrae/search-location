package hama.searchlocation.location.application

import hama.searchlocation.location.domain.LocationQueryAggregator
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class LocationApplication(
    private val locationQueryAggregator: LocationQueryAggregator,
    private val locationSearchKafkaTemplate: KafkaTemplate<String, String>
) {
    fun getLocations(keyword: String): List<String> {
        val locations = locationQueryAggregator.query(keyword)
        locationSearchKafkaTemplate.sendDefault(keyword, locations.toString())
        return locations
    }
}