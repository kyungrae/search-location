package hama.searchlocation.location.domain

interface LocationFallbackRepository {
    fun save(keyword: String, locations: String)
}