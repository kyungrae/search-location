package hama.searchlocation.keyword.application

import hama.searchlocation.keyword.domain.KeywordSearchLogRepository
import hama.searchlocation.keyword.domain.KeywordSearchLog
import org.springframework.stereotype.Service

@Service
class KeywordLogAccumulateApplication(
    private val keywordHistogramRepository: KeywordSearchLogRepository
) {
    fun accumulate(keywords: List<String>) {
        val keywordSearchLogs = keywords.fold(mutableMapOf<String, Int>()) { acc, keyword ->
            acc[keyword] = acc.getOrDefault(keyword, 0) + 1
            acc
        }.map { KeywordSearchLog(it.key, it.value.toLong()) }
        keywordHistogramRepository.accumulate(keywordSearchLogs)
    }
}