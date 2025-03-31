package com.example.swift.application.dto

data class BankDTO(
    val address: String? = "",
    val bankName: String,
    val countryISO2: String,
    val countryName: String,
    val isHeadquarter: Boolean,
    val swiftCode: String,
)