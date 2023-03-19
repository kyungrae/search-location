package hama.searchlocation.keyword.ui

import hama.searchlocation.keyword.application.KeywordLogAccumulateApplication
import hama.searchlocation.location.config.LocationKafkaConfig.Companion.LOCATION_SEARCH_TOPIC
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KeywordListener(
    private val keywordLogAccumulateApplication: KeywordLogAccumulateApplication
) {
    @KafkaListener(
        topics = [LOCATION_SEARCH_TOPIC],
        containerFactory = "keywordKafkaListenerContainerFactory",
        properties = ["max.poll.interval.ms=1000", "max.poll.records=500"]
    )
    fun listen(records: List<ConsumerRecord<String, String>>) {
        val keywords = records.stream().toList().map { it.key() }
        keywordLogAccumulateApplication.accumulate(keywords)
    }
}