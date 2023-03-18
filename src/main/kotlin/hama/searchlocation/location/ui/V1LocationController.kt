package hama.searchlocation.location.ui

import hama.searchlocation.location.application.LocationApplication
import jakarta.validation.ConstraintViolationException
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/v1/locations")
class V1LocationController(
    private val locationApplication: LocationApplication
) {
    @GetMapping
    fun getLocations(
        @NotBlank @RequestParam("keyword") keyword: String
    ): List<String> {
        return locationApplication.getLocations(keyword)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationExceptionException(
        e: ConstraintViolationException
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse(e.localizedMessage))
}