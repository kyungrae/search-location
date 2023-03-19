package hama.searchlocation.keyword.domain

interface KeywordSearchLogRepository {
    fun accumulate(logs: List<KeywordSearchLog>)
}