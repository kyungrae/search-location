package hama.searchlocation.keyword.application

import hama.searchlocation.keyword.domain.KeywordSearchLog
import hama.searchlocation.keyword.domain.KeywordSearchLogRepository
import org.springframework.stereotype.Service

@Service
class KeywordLogQueryApplication(
    private val keywordSearchLogRepository: KeywordSearchLogRepository
) {
    fun getPopularKeywords(): List<KeywordSearchLog> =
        keywordSearchLogRepository.findTop10OrderBySearchCountDesc()
}