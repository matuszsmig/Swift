package com.example.swift.adapters.constrollers

import com.example.swift.adapters.services.SwiftCodeService
import com.example.swift.application.dto.BankDTO
import com.example.swift.application.dto.CountryISO2CodeSummaryDTO
import com.example.swift.application.dto.SwiftCodeSummaryDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/swift-codes")
class SwiftCodeController(private val swiftCodeService: SwiftCodeService) {

    @GetMapping(path = ["/{swift-code}"])
    fun listAllBanksBySwiftCode(@PathVariable("swift-code") swiftCode: String): ResponseEntity<SwiftCodeSummaryDTO> {
        return ResponseEntity(swiftCodeService.getAllBanksBySwiftCode(swiftCode), HttpStatus.OK)
    }

    @GetMapping(path = ["/country/{countryISO2code}"])
    fun listAllBanksByISO2Code(@PathVariable("countryISO2code") id: String): ResponseEntity<CountryISO2CodeSummaryDTO> {
        return ResponseEntity(swiftCodeService.getAllBanksByCountryISO2Code(id), HttpStatus.OK)
    }

    @PostMapping
    fun addBankToSwift(@RequestBody bankDTO: BankDTO): ResponseEntity<String> {
        return ResponseEntity(swiftCodeService.addBankToSwift(bankDTO), HttpStatus.OK)
    }

    @DeleteMapping(path = ["/{swift-code}"])
    fun deleteBank(@PathVariable("swift-code") swiftCode: String): ResponseEntity<String> {
        return ResponseEntity(swiftCodeService.deleteBank(swiftCode), HttpStatus.OK)
    }
}

