package hama.searchlocation.keyword.config

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties

@Configuration
class KeywordKafkaConfig(
    private val kafkaProperties: KafkaProperties
) {
    @Bean
    fun keywordConsumerConfigs(): Map<String, Any> {
        val props = mutableMapOf<String, Any>()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaProperties.bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.GROUP_ID_CONFIG] = "keyword-search"
        return props
    }

    @Bean
    fun keywordConsumerFactory(): ConsumerFactory<String, String> =
        DefaultKafkaConsumerFactory(keywordConsumerConfigs())

    @Bean
    fun keywordKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, String> {
        val keywordContainerFactory = ConcurrentKafkaListenerContainerFactory<String, String>()
        keywordContainerFactory.consumerFactory = keywordConsumerFactory()
        keywordContainerFactory.containerProperties.ackMode = ContainerProperties.AckMode.BATCH
        keywordContainerFactory.setConcurrency(1) // 주의 concurrency 설정하면 keyword 갱신할 떄 경합 문제 가능성 있음
        keywordContainerFactory.isBatchListener = true
        return keywordContainerFactory
    }
}