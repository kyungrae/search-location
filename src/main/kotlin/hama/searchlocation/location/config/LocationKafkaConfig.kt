package hama.searchlocation.location.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class LocationKafkaConfig(
    private val kafkaProperties: KafkaProperties,
) {
    @Bean
    fun locationProducerProps(): Map<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.ACKS_CONFIG] = "0" // 유실되도 크게 중요하지 않은 이벤트이기 떄문
        return props
    }

    @Bean
    fun locationProducerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(locationProducerProps())

    @Bean
    fun locationKafkaTemplate(): KafkaTemplate<String, String> {
        val locationSearchKafkaTemplate = KafkaTemplate(locationProducerFactory())
        locationSearchKafkaTemplate.defaultTopic = "location.search.success"
        return locationSearchKafkaTemplate
    }
}