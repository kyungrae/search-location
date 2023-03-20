package hama.searchlocation.keyword.infrastructure

import hama.searchlocation.keyword.domain.KeywordSearchLog
import hama.searchlocation.keyword.domain.KeywordSearchLogRepository
import org.springframework.jdbc.core.JdbcTemplate

class KeywordSearchLogRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : KeywordSearchLogRepository {
    override fun accumulate(logs: List<KeywordSearchLog>) {
        // 이미 존재하는 keyword_search_log 검색
        val inClauses = List(logs.size) { "?" }.joinToString(",")
        val alreadyExistKeywordLogs = jdbcTemplate.query(
            "SELECT keyword, search_count FROM keyword_search_log WHERE keyword IN ($inClauses)",
            { rs, _ -> KeywordSearchLog(rs.getString("keyword"), rs.getLong("search_count")) },
            *logs.map { it.keyword }.toTypedArray()
        ).sortedBy { it.keyword }
        val alreadyExistKeywordSet = alreadyExistKeywordLogs.map { it.keyword }.toSet()

        // 기존에 존재하는 keyword_search_log batch 업데이트
        val baseKeywordLogIncrements = logs
            .filter { log -> alreadyExistKeywordSet.contains(log.keyword) }
            .sortedBy { it.keyword }
        jdbcTemplate.batchUpdate(
            "UPDATE keyword_search_log SET search_count = ? WHERE keyword = ?",
            alreadyExistKeywordLogs
                .zip(baseKeywordLogIncrements)
                .map { (a, b) -> a + b }
                .map { arrayOf(it.searchCount, it.keyword) }
        )

        // 신규 keyword_search_log 추가
        val newKeywordSearchLog = logs
            .filterNot { log -> alreadyExistKeywordSet.contains(log.keyword) }
        jdbcTemplate.batchUpdate(
            "INSERT INTO keyword_search_log (keyword, search_count) VALUES (?,?)",
            newKeywordSearchLog
                .map { arrayOf(it.keyword, it.searchCount) }
        )
    }
}