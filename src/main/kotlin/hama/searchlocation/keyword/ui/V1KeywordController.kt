package hama.searchlocation.keyword.ui

import hama.searchlocation.keyword.application.KeywordLogQueryApplication
import hama.searchlocation.keyword.domain.KeywordSearchLog
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/keywords")
class V1KeywordController(
    private val keywordLogQueryApplication: KeywordLogQueryApplication
) {
    @GetMapping("/popular")
    fun getPopularKeywords(): List<KeywordSearchLog> =
        keywordLogQueryApplication.getPopularKeywords()
}