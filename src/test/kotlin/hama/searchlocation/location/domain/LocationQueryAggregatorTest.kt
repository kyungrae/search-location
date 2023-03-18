package hama.searchlocation.location.domain

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LocationQueryAggregatorTest {
    private val kakaoLocationQueryClient = mockk<LocationQueryClient>()
    private val naverLocationQueryClient = mockk<LocationQueryClient>()
    private val locationQueryAggregator = LocationQueryAggregator(
        kakaoLocationQueryClient = kakaoLocationQueryClient,
        naverLocationQueryClient = naverLocationQueryClient
    )

    @Test
    fun both_kakao_and_naver_locations_size_is_5_and_same_location_is_0() {
        // given
        val keyword = "은행"
        every { kakaoLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("우리은행"),
            Location("국민은행"),
            Location("부산은행"),
            Location("새마을금고")
        )
        every { naverLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("한국은행"),
            Location("농협은행"),
            Location("하나은행"),
            Location("신한은행"),
            Location("기업은행")
        )

        // when
        val results = locationQueryAggregator.query(keyword)

        // then
        assertEquals(10, results.size)
        assertIterableEquals(
            listOf("카카오뱅크", "우리은행", "국민은행", "부산은행", "새마을금고"),
            results.subList(0, 5)
        )
        assertIterableEquals(
            listOf("한국은행", "농협은행", "하나은행", "신한은행", "기업은행"),
            results.drop(5)
        )
    }

    @Test
    fun both_kakao_and_naver_locations_size_is_5_and_same_location_is_3() {
        // given
        val keyword = "은행"
        every { kakaoLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("국민은행"),
            Location("부산은행"),
            Location("우리은행"),
            Location("새마을금고")
        )
        every { naverLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("국민은행"),
            Location("부산은행"),
            Location("하나은행"),
            Location("기업은행")
        )

        // when
        val results = locationQueryAggregator.query(keyword)

        // then
        assertEquals(7, results.size)
        assertIterableEquals(
            listOf("카카오뱅크", "국민은행", "부산은행"),
            results.subList(0, 3)
        )
        assertIterableEquals(
            listOf("우리은행", "새마을금고"),
            results.subList(3, 5)
        )
        assertIterableEquals(
            listOf("하나은행", "기업은행"),
            results.subList(5, 7)
        )
    }


    @Test
    fun both_kakao_and_naver_locations_size_is_5_and_same_location_is_5() {
        // given
        val keyword = "은행"
        every { kakaoLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("우리은행"),
            Location("국민은행"),
            Location("부산은행"),
            Location("새마을금고")
        )
        every { naverLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("우리은행"),
            Location("국민은행"),
            Location("부산은행"),
            Location("새마을금고")
        )

        // when
        val results = locationQueryAggregator.query(keyword)

        // then
        assertEquals(5, results.size)
    }

    @Test
    fun kakao_locations_size_is_7_and_naver_locations_size_is_3() {
        // given
        val keyword = "은행"
        every { kakaoLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("국민은행"),
            Location("부산은행"),
            Location("우리은행"),
            Location("새마을금고")
        )
        every { naverLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("기업은행"),
            Location("신한은행")
        )
        every { kakaoLocationQueryClient.getLocations(keyword, 2, 2) } returns listOf(
            Location("신한은행"),
            Location("하나은행")
        )

        // when
        val results = locationQueryAggregator.query(keyword)

        // then
        assertEquals(8, results.size)
        assertIterableEquals(
            listOf("카카오뱅크", "신한은행"),
            results.subList(0, 2)
        )
        assertIterableEquals(
            listOf("국민은행","부산은행","우리은행","새마을금고","하나은행"),
            results.subList(2, 7)
        )
        assertIterableEquals(
            listOf("기업은행"),
            results.drop(7)
        )
    }


    @Test
    fun kakao_locations_size_is_3_and_naver_locations_size_is_7() {
        // given
        val keyword = "은행"
        every { kakaoLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("기업은행"),
            Location("신한은행")
        )
        every { naverLocationQueryClient.getLocations(keyword) } returns listOf(
            Location("카카오뱅크"),
            Location("국민은행"),
            Location("부산은행"),
            Location("우리은행"),
            Location("새마을금고")
        )
        every { naverLocationQueryClient.getLocations(keyword, 2, 2) } returns listOf(
            Location("신한은행"),
            Location("하나은행")
        )

        // when
        val results = locationQueryAggregator.query(keyword)

        // then
        assertEquals(8, results.size)
        assertIterableEquals(
            listOf("카카오뱅크", "신한은행"),
            results.subList(0, 2)
        )
        assertIterableEquals(
            listOf("기업은행"),
            results.subList(2, 3)
        )
        assertIterableEquals(
            listOf("국민은행","부산은행","우리은행","새마을금고","하나은행"),
            results.drop(3)
        )
    }
}