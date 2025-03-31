package com.example.swift.application.dto

data class BankSummaryDTO(
    val address: String? = "",
    val bankName: String,
    val countryISO2: String,
    val countryName: String,
    val isHeadquarter: Boolean,
    val swiftCode: String,
    val branches: List<SingleBankDTO>,
) : SwiftCodeSummaryDTO()