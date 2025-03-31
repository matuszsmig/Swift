package com.example.swift.adapters.services

import com.example.swift.application.dto.BankDTO
import com.example.swift.application.dto.CountryISO2CodeSummaryDTO
import com.example.swift.application.dto.SwiftCodeSummaryDTO
import jakarta.transaction.Transactional

interface SwiftCodeService {
    fun getAllBanksBySwiftCode(swiftCode: String): SwiftCodeSummaryDTO

    fun getAllBanksByCountryISO2Code(countryISO2Code: String): CountryISO2CodeSummaryDTO

    @Transactional
    fun addBankToSwift(bankDTO: BankDTO): String

    fun deleteBank(swiftCode: String): String
}