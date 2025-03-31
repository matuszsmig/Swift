package com.example.swift.application.dto

data class SingleBankDTO(
    val address: String? = "",
    val bankName: String,
    val countryISO2: String,
    val isHeadquarter: Boolean,
    val swiftCode: String,
)