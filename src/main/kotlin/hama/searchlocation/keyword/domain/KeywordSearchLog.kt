package hama.searchlocation.keyword.domain

class KeywordSearchLog(
    val keyword: String,
    val searchCount: Long
) {
    operator fun plus(other: KeywordSearchLog): KeywordSearchLog {
        return if (keyword == other.keyword)
            KeywordSearchLog(keyword, searchCount + other.searchCount)
        else
            this
    }
}