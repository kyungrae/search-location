package hama.searchlocation.location.domain

interface LocationQueryClient {
    fun getLocations(keyword: String, page: Int = 1, size: Int = 5): List<Location>
}