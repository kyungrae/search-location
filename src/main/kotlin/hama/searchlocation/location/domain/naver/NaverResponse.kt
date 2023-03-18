package hama.searchlocation.location.domain.naver

data class NaverResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<NaverItem>
) {
    val locations: List<String>
        get() = items.map { it.title }

    data class NaverItem(
        val title: String,
        val link: String,
        val category: String,
        val description: String,
        val telephone: String,
        val address: String,
        val roadAddress: String,
        val mapx: String,
        val mapy: String
    )
}
