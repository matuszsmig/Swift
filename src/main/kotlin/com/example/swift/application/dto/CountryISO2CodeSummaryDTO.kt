package com.example.swift.application.dto

data class CountryISO2CodeSummaryDTO(
    val countryISO2: String,
    val countryName: String,
    val swiftCodes: List<SingleBankDTO>,
)