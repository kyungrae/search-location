package hama.searchlocation.keyword.domain

interface KeywordSearchLogRepository {
    fun accumulate(logs: List<KeywordSearchLog>)
    fun findTop10OrderBySearchCountDesc(): List<KeywordSearchLog>
}