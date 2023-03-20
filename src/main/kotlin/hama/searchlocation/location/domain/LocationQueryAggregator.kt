package hama.searchlocation.location.domain

class LocationQueryAggregator(
    private val kakaoLocationQueryClient: LocationQueryClient,
    private val naverLocationQueryClient: LocationQueryClient,
) {
    /**
     * 클라이언트 검색 결과가 부족한 경우 다른 클라이언트에서 내용을 보충하는 요구 사항이 있다.
     * 각 클라이언트는 다른 클라이언트의 결과에 의존적이라 쿼리 결과를 가져오는 로직의 응집성이 부족하다.
     */
    fun query(keyword: String): List<String> {
        val kakaoLocations = kakaoLocationQueryClient.getLocations(keyword).toMutableList()
        val naverLocations = naverLocationQueryClient.getLocations(keyword).toMutableList()
        if (kakaoLocations.size == QUERY_SIZE && naverLocations.size < QUERY_SIZE) {
            val spareLocations = kakaoLocationQueryClient.getLocations(keyword, NEXT_PAGE, QUERY_SIZE - naverLocations.size)
            kakaoLocations.addAll(spareLocations)
        }
        if (kakaoLocations.size < QUERY_SIZE && naverLocations.size == QUERY_SIZE) {
            val spareLocations = naverLocationQueryClient.getLocations(keyword, NEXT_PAGE, QUERY_SIZE - kakaoLocations.size)
            naverLocations.addAll(spareLocations)
        }

        return union(kakaoLocations, naverLocations)
    }

    private fun union(firstPriorities: List<Location>, secondPriorities: List<Location>): List<String> {
        val kakaoLocationSet = firstPriorities.toSet()
        val naverLocationSet = secondPriorities.toSet()
        val intersectSet = kakaoLocationSet intersect naverLocationSet

        val onlyKakao = (kakaoLocationSet - intersectSet).toList()
        val onlyNaver = (naverLocationSet - intersectSet).toList()

        return (intersectSet.toList() + onlyKakao + onlyNaver).map { it.name }
    }

    companion object {
        const val QUERY_SIZE = 5
        const val NEXT_PAGE = 2
    }
}