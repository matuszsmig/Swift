package com.example.swift.adapters.services.impl

import com.example.swift.*
import com.example.swift.adapters.repositories.BankBranchRepository
import com.example.swift.adapters.repositories.BankRepository
import com.example.swift.adapters.repositories.CountryRepository
import com.example.swift.adapters.services.SwiftCodeService
import com.example.swift.adapters.services.impl.validators.SwiftCodeValidator
import com.example.swift.application.dto.BankDTO
import com.example.swift.application.dto.CountryISO2CodeSummaryDTO
import com.example.swift.application.dto.SwiftCodeSummaryDTO
import com.example.swift.exceptions.InvalidSwiftCodeException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.stereotype.Service

const val HEADQUARTER_SWIFT_CODE_SUFFIX = "XXX"

@Service
class SwiftCodeServiceImpl(
    private val bankRepository: BankRepository,
    private val bankBranchRepository: BankBranchRepository,
    private val countryRepository: CountryRepository,
    private val swiftCodeValidator: SwiftCodeValidator
): SwiftCodeService {
    fun findBankBySwiftCode(swiftCode: String) = bankRepository.findByIdOrNull(swiftCode)
    fun findBankBranchBySwiftCode(swiftCode: String) = bankBranchRepository.findByIdOrNull(swiftCode)

    fun isHeadquarter(swiftCode: String) = swiftCode.endsWith(HEADQUARTER_SWIFT_CODE_SUFFIX)

    override fun getAllBanksBySwiftCode(swiftCode: String): SwiftCodeSummaryDTO {
        swiftCodeValidator.validateSwiftCode(swiftCode, isHeadquarter(swiftCode))

        if (isHeadquarter(swiftCode)) {
            val bank = findBankBySwiftCode(swiftCode)
                ?: throw ResourceNotFoundException("Bank headquarter not found")

            val countryName = bank.countryIso2Code.name
            val bankBranches = bank.bankBranchesEntities.map { it.toBankDTO(countryName) }

            return bank.toBankSummaryDTO(bankBranches, countryName)
        }

        val bankBranch = findBankBranchBySwiftCode(swiftCode)
            ?: throw ResourceNotFoundException("Bank branch not found")

        val countryName = bankBranch.countryIso2Code.name

        return bankBranch.toBankBranchSummaryDTO(countryName)
    }

    override fun getAllBanksByCountryISO2Code(countryISO2Code: String): CountryISO2CodeSummaryDTO {
        swiftCodeValidator.validateISO2Code(countryISO2Code)

        val country = countryRepository.findByIdOrNull(countryISO2Code)
            ?: throw ResourceNotFoundException("Country not found")

        val banks = country.bankEntities.map { it.toBankDTO(countryISO2Code) }
        val bankBranches = country.bankBranchesEntities.map { it.toBankDTO(countryISO2Code) }

        return country.toCountryISO2CodeSummaryDTO(banks, bankBranches)
    }

    override fun addBankToSwift(bankDTO: BankDTO): String {
        val country = swiftCodeValidator.validateCountry(bankDTO.countryISO2, bankDTO.countryName)
        swiftCodeValidator.validateSwiftCode(bankDTO.swiftCode, bankDTO.isHeadquarter)

        val swiftCode = bankDTO.swiftCode

        if (isHeadquarter(swiftCode)) {
            val bank = findBankBySwiftCode(swiftCode)

            if (bank != null) {
                throw InvalidSwiftCodeException("Bank already exists")
            }

            val newBank = bankRepository.save(
                bankDTO.toBankEntity(country)
            )

            return "Bank headquarter with Swift code ${newBank.swiftCode} has been added to the system"

        } else {
            val bankBranch = findBankBranchBySwiftCode(swiftCode)
            val bankHeadquarter = bankRepository.findByIdOrNull(swiftCode.substring(0, 8) + HEADQUARTER_SWIFT_CODE_SUFFIX)
                ?: throw ResourceNotFoundException("Headquarter bank not found")

            if (bankBranch != null) {
                throw InvalidSwiftCodeException("Bank already exists")
            }

            val newBank = bankBranchRepository.save(
                bankDTO.toBankBranchEntity(country, bankHeadquarter)
            )

            return "Bank branch with Swift code ${newBank.swiftCode} has been added to the system"
        }
    }

    override fun deleteBank(swiftCode: String): String {
        swiftCodeValidator.validateSwiftCode(swiftCode, isHeadquarter(swiftCode))

        if (isHeadquarter(swiftCode)) {
            val bank = findBankBySwiftCode(swiftCode)
                ?: throw ResourceNotFoundException("Bank headquarter not found")

            bankRepository.delete(bank)

            return "Bank headquarter with Swift code ${bank.swiftCode} has been deleted from the system and it's branches"
        }

        val bankBranch = findBankBranchBySwiftCode(swiftCode)
            ?: throw ResourceNotFoundException("Bank branch not found")

        bankBranchRepository.delete(bankBranch)

        return "Bank branch with Swift code ${bankBranch.swiftCode} has been deleted from the system"
    }
}