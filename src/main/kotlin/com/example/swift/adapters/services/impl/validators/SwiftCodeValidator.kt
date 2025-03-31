package com.example.swift.adapters.services.impl.validators

import com.example.swift.adapters.repositories.CountryRepository
import com.example.swift.domain.model.entities.CountryEntity
import com.example.swift.exceptions.InvalidISO2CodeException
import com.example.swift.exceptions.InvalidSwiftCodeException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.stereotype.Component

@Component
class SwiftCodeValidator(
    private val countryRepository: CountryRepository
) {
    fun validateCountry(countryISO2: String, countryName: String): CountryEntity {
        val country = countryRepository.findByIdOrNull(countryISO2)
            ?: throw ResourceNotFoundException("Country not found")

        if (country.name != countryName) {
            throw InvalidISO2CodeException("Country name does not match the ISO2 code")
        }

        return country
    }

    fun validateSwiftCode(swiftCode: String, isHeadquarter: Boolean) {
        if (swiftCode.length != 11) {
            throw InvalidSwiftCodeException("Incorrect SWIFT code length")
        }

        if (!swiftCode.all { it.isUpperCase() || it.isDigit() }) {
            throw InvalidSwiftCodeException("Incorrect SWIFT code format")
        }

        if (swiftCode.endsWith("XXX") && !isHeadquarter) {
            throw InvalidSwiftCodeException("Headquarter bank should end with XXX")
        }

        if (!swiftCode.endsWith("XXX") && isHeadquarter) {
            throw InvalidSwiftCodeException("Branch bank should not end with XXX")
        }
    }

    fun validateISO2Code(iso2Code: String) {
        if (iso2Code.length != 2) {
            throw InvalidISO2CodeException("Incorrect ISO2 code length")
        }

        if (!iso2Code.all { it.isUpperCase() }) {
            throw InvalidISO2CodeException("Incorrect ISO2 code format")
        }
    }
}