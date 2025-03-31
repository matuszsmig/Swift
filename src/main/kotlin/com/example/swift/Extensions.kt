package com.example.swift

import com.example.swift.application.dto.*
import com.example.swift.domain.model.entities.BankBranchEntity
import com.example.swift.domain.model.entities.BankEntity
import com.example.swift.domain.model.entities.CountryEntity

fun BankBranchEntity.toBankBranchSummaryDTO(countryName: String) = BankBranchSummaryDTO(
    address = this.address?.trim(),
    bankName = this.name,
    countryISO2 = this.countryIso2Code.iso2Code,
    countryName = countryName,
    isHeadquarter = this.isHeadquarter,
    swiftCode = this.swiftCode
)

fun BankEntity.toBankSummaryDTO(branches: List<SingleBankDTO>, countryName: String) = BankSummaryDTO(
    address = this.address?.trim(),
    bankName = this.name,
    countryISO2 = this.countryIso2Code.iso2Code,
    countryName = countryName,
    isHeadquarter = this.isHeadquarter,
    swiftCode = this.swiftCode,
    branches = branches
)

fun BankEntity.toBankDTO(iso2Code: String) = SingleBankDTO(
    address = this.address?.trim(),
    bankName = this.name,
    countryISO2 = iso2Code,
    isHeadquarter = this.isHeadquarter,
    swiftCode = this.swiftCode
)

fun BankBranchEntity.toBankDTO(iso2Code: String) = SingleBankDTO(
    address = this.address?.trim(),
    bankName = this.name,
    countryISO2 = iso2Code,
    isHeadquarter = this.isHeadquarter,
    swiftCode = this.swiftCode
)

fun CountryEntity.toCountryISO2CodeSummaryDTO(banks: List<SingleBankDTO>, branches: List<SingleBankDTO>) = CountryISO2CodeSummaryDTO(
    countryISO2 = this.iso2Code,
    countryName = this.name,
    swiftCodes = banks + branches
)

fun BankDTO.toBankEntity(country: CountryEntity) = BankEntity(
    swiftCode = this.swiftCode,
    name = this.bankName,
    address = this.address,
    isHeadquarter = this.isHeadquarter,
    countryIso2Code = country
)

fun BankDTO.toBankBranchEntity(country: CountryEntity, headquarter: BankEntity) = BankBranchEntity(
    swiftCode = this.swiftCode,
    name = this.bankName,
    address = this.address,
    isHeadquarter = this.isHeadquarter,
    countryIso2Code = country,
    headquartersSwiftCode = headquarter
)

fun BankBranchEntity.toSingleBankDTO() = SingleBankDTO(
    address = this.address?.trim(),
    bankName = this.name,
    countryISO2 = this.countryIso2Code.iso2Code,
    isHeadquarter = this.isHeadquarter,
    swiftCode = this.swiftCode
)

fun BankEntity.toSingleBankDTO() = SingleBankDTO(
    address = this.address?.trim(),
    bankName = this.name,
    countryISO2 = this.countryIso2Code.iso2Code,
    isHeadquarter = this.isHeadquarter,
    swiftCode = this.swiftCode
)