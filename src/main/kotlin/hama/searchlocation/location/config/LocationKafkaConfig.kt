package hama.searchlocation.location.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*

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
        props[ProducerConfig.ACKS_CONFIG] = "0" // 유실되도 크게 중요하지 않은 이벤트
        return props
    }

    @Bean
    fun locationProducerFactory(): ProducerFactory<String, String> =
        DefaultKafkaProducerFactory(locationProducerProps())

    @Bean
    fun locationKafkaTemplate(): KafkaTemplate<String, String> {
        val locationSearchKafkaTemplate = KafkaTemplate(locationProducerFactory())
        locationSearchKafkaTemplate.defaultTopic = LOCATION_SEARCH_TOPIC
        return locationSearchKafkaTemplate
    }
    @Bean
    fun locationConsumerConfigs(): Map<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "location-fallback"
        return props
    }

    @Bean
    fun locationConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(locationConsumerConfigs())

    @Bean
    fun locationKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val locationContainerFactory = ConcurrentKafkaListenerContainerFactory<String, String>()
        locationContainerFactory.consumerFactory = locationConsumerFactory()
        locationContainerFactory.setConcurrency(1)
        return locationContainerFactory
    }

    companion object {
        const val LOCATION_SEARCH_TOPIC = "location.search.success"
    }
}