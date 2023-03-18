package hama.searchlocation.location.domain

interface LocationQueryClient {
    fun getLocations(keyword: String): List<Location>
}