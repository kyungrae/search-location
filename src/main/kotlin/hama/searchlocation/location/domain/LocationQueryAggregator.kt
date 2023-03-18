package hama.searchlocation.location.domain

class LocationQueryAggregator(
    private val kakaoLocationQueryClient: LocationQueryClient,
    private val naverLocationQueryClient: LocationQueryClient,
) {
    fun query(keyword: String): List<String> {
        val kakaoLocations = kakaoLocationQueryClient.getLocations(keyword)
        val naverLocations = naverLocationQueryClient.getLocations(keyword)

        val kakaoLocationSet = kakaoLocations.toSet()
        val naverLocationSet = naverLocations.toSet()
        val intersectSet = kakaoLocationSet intersect naverLocationSet
        val onlyKakaoSet = kakaoLocationSet - intersectSet
        val onlyNaverSet = naverLocationSet - intersectSet

        return (intersectSet.toList() + onlyKakaoSet.toList() + onlyNaverSet.toList()).map { it.name }
    }
}