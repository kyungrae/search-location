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
        val baseKeywordLogs = jdbcTemplate.query(
            "SELECT keyword, search_count FROM keyword_search_log WHERE keyword IN ($inClauses)",
            { rs, _ -> KeywordSearchLog(rs.getString("keyword"), rs.getLong("search_count")) },
            *logs.map { it.keyword }.toTypedArray()
        ).sortedBy { it.keyword }

        // 가존에 존재하는 keyword_search_log 업데이트
        val baseKeywordLogIncrements = logs
            .filter { log -> baseKeywordLogs.map { it.keyword }.contains(log.keyword) }
            .sortedBy { it.keyword }

        baseKeywordLogs.zip(baseKeywordLogIncrements)
            .map { (a, b) -> a + b }
            .forEach {
                jdbcTemplate.update(
                    "UPDATE keyword_search_log SET search_count = ? WHERE keyword = ?",
                    it.searchCount,
                    it.keyword
                )
            }

        // 신규 keyword_search_log 추가
        val newKeywordSearchLog = logs
            .filterNot { log -> baseKeywordLogs.map { it.keyword }.contains(log.keyword) }
            .sortedBy { it.keyword }
        val newRecords = List(newKeywordSearchLog.size) { "(?,?)" }.joinToString(",")
        if (newRecords.isNotEmpty()) {
            jdbcTemplate.update(
                "INSERT INTO keyword_search_log (keyword, search_count) VALUES $newRecords",
                *newKeywordSearchLog.flatMap { listOf(it.keyword, it.searchCount) }.toTypedArray()
            )
        }
    }
}