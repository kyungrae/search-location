package hama.searchlocation.keyword.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class KeywordSearchLogTest {

    @Test
    fun if_keywordSearchLog_has_same_keyword__they_can_be_added() {
        val hama1 = KeywordSearchLog("hama", 1)
        val hama2 = KeywordSearchLog("hama", 2)
        val sumHama = hama1 + hama2
        assertEquals(sumHama.searchCount, hama1.searchCount + hama2.searchCount)
    }

    @Test
    fun if_keywordSearchLog_has_different_keyword__they_cannot_be_added() {
        val hama = KeywordSearchLog("hama", 1)
        val rhino = KeywordSearchLog("rhino", 2)
        val afterHama = hama + rhino
        assertEquals(afterHama.searchCount, hama.searchCount)
    }
}