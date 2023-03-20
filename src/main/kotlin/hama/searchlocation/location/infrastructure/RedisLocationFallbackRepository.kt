package hama.searchlocation.location.infrastructure

import hama.searchlocation.location.domain.LocationFallbackRepository
import org.springframework.data.redis.core.RedisTemplate

class RedisLocationFallbackRepository(
    redisTemplate: RedisTemplate<String, String>
) : LocationFallbackRepository {
    private val valueOperations = redisTemplate.opsForValue()

    override fun save(keyword: String, locations: String) =
        valueOperations.set(keyword, locations)
}