package hama.searchlocation.location.ui

import hama.searchlocation.location.application.LocationFallbackApplication
import hama.searchlocation.location.config.LocationKafkaConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class LocationFallbackConsumer(
    private val locationFallbackApplication: LocationFallbackApplication
) {
    @KafkaListener(
        id = LOCATION_FALLBACK_CONTAINER_ID,
        topics = [LocationKafkaConfig.LOCATION_SEARCH_TOPIC],
        containerFactory = "locationKafkaListenerContainerFactory",
    )
    fun listen(record: ConsumerRecord<String, String>) {
        locationFallbackApplication.save(record.key(), record.value())
    }

    companion object {
        const val LOCATION_FALLBACK_CONTAINER_ID = "location-fallback"
    }
}