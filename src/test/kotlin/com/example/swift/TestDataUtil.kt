package com.example.swift

import com.example.swift.application.dto.BankDTO
import com.example.swift.domain.model.entities.BankBranchEntity
import com.example.swift.domain.model.entities.BankEntity
import com.example.swift.domain.model.entities.CountryEntity

fun testCountryEntityA() = CountryEntity(
    iso2Code = "AA",
    name = "ACOUNTRY",
    timeZone = "Europe/London",
)

fun testBankEntityA(swiftCode: String? = "ABCD1234XXX") = BankEntity(
    swiftCode = swiftCode!!,
    name = "Test bank headquarter name A",
    address = "Address bank name A",
    isHeadquarter = true,
    countryIso2Code = testCountryEntityA(),
)

fun testBankBranchEntityA(swiftCode: String? = "ABCD1234X22") = BankBranchEntity(
    swiftCode = swiftCode!!,
    name = "Test bank branch name A",
    address = "Address bank branch name A",
    isHeadquarter = false,
    countryIso2Code = testCountryEntityA(),
    headquartersSwiftCode = testBankEntityA()
)

fun testBankDTOA(
    swiftCode: String? = "ABCD1234X22",
    isHeadquarter: Boolean? = false,
    countryISO2: String? = "AA",
    countryName: String? = "ACOUNTRY"
) = BankDTO(
    address = "Address bank branch name A",
    bankName = "Test bank branch name A",
    countryISO2 = countryISO2!!,
    countryName = countryName!!,
    isHeadquarter = isHeadquarter!!,
    swiftCode = swiftCode!!
)

fun testBankHeadquarterDTOA(
    swiftCode: String? = "ABCD1234XXX",
    countryISO2: String? = "AA",
    countryName: String? = "ACOUNTRY"
) = BankDTO(
    address = "Address bank name A",
    bankName = "Test bank headquarter name A",
    countryISO2 = countryISO2!!,
    countryName = countryName!!,
    isHeadquarter = true,
    swiftCode = swiftCode!!
)

fun testBankBranchDTOA(
    swiftCode: String? = "ABCD1234X22",
    countryISO2: String? = "AA",
    countryName: String? = "ACOUNTRY"
) = BankDTO(
    address = "Address bank branch name A",
    bankName = "Test bank branch name A",
    countryISO2 = countryISO2!!,
    countryName = countryName!!,
    isHeadquarter = false,
    swiftCode = swiftCode!!
)