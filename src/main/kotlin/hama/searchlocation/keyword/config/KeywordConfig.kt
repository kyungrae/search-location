package hama.searchlocation.keyword.config

import hama.searchlocation.keyword.domain.KeywordSearchLogRepository
import hama.searchlocation.keyword.infrastructure.KeywordSearchLogRepositoryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class KeywordConfig(
    private val jdbcTemplate: JdbcTemplate
) {
    @Bean
    fun keywordSearchLogRepository(): KeywordSearchLogRepository {
        return KeywordSearchLogRepositoryImpl(jdbcTemplate)
    }
}