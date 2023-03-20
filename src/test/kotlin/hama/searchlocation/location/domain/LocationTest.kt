package hama.searchlocation.location.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LocationTest {
    @Test
    fun location_object_name_cannot_contain_tag_string() {
        val pharmacy = Location("<b>태양온누리약국</b>")
        assertEquals("태양온누리약국", pharmacy.name)
    }

    @Test
    fun if_two_objects_have_same_name__they_are_identical() {
        val normalPharmacy = Location("태양온누리약국")
        val boldPharmacy = Location("<b>태양온누리약국</b>")

        assertEquals(normalPharmacy, boldPharmacy)
    }

    @Test
    fun if_two_objects_have_different_name__they_are_different() {
        val kakaoBank = Location("카카오뱅크")
        val tossBank = Location("토스뱅크")

        assertNotEquals(kakaoBank, tossBank)
    }
}