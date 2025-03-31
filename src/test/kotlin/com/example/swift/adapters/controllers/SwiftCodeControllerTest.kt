package com.example.swift.adapters.controllers

import com.example.swift.*
import com.example.swift.adapters.services.SwiftCodeService
import com.example.swift.application.dto.SingleBankDTO
import com.example.swift.exceptions.InvalidISO2CodeException
import com.example.swift.exceptions.InvalidSwiftCodeException
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

private const val SWIFT_CODE_URL = "/v1/swift-codes"
private const val ISO2_CODE_URL = "/v1/swift-codes/country"
private const val A_TEST_SWIFT_CODE_BRANCH = "ABCD1234X22"
private const val A_TEST_SWIFT_CODE_HEADQUARTER = "ABCD1234XXX"

@SpringBootTest
@AutoConfigureMockMvc
class SwiftCodeControllerTest @Autowired constructor (
    private val mockMvc: MockMvc,
    @MockkBean(relaxed = true) val swiftCodeService: SwiftCodeService
) {
    val objectMapper = ObjectMapper()

    @Test
    fun `test that list all banks by swift code endpoint will return 400 status code when swift code length is incorrect`() {
        val swiftCode = "XXX"

        every {
            swiftCodeService.getAllBanksBySwiftCode(swiftCode)
        } throws InvalidSwiftCodeException("Incorrect SWIFT code length")

        mockMvc.get("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Incorrect SWIFT code length") }
        }
    }

    @Test
    fun `test that list all banks by swift code endpoint will return 400 status code when swift code format is incorrect`() {
        val swiftCode = "XXXX1234abc"

        every {
            swiftCodeService.getAllBanksBySwiftCode(swiftCode)
        } throws InvalidSwiftCodeException("Incorrect SWIFT code format")

        mockMvc.get("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Incorrect SWIFT code format") }
        }
    }

    @Test
    fun `test that list all banks by swift code endpoint will return 404 status code when swift code do not match bank headquarter`() {
        val swiftCode = "ABCD1234XXX"

        every {
            swiftCodeService.getAllBanksBySwiftCode(swiftCode)
        } throws ResourceNotFoundException("Bank headquarter not found")

        mockMvc.get("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { string("Resource not found: Bank headquarter not found") }
        }
    }

    @Test
    fun `test that list all banks by swift code endpoint will return 404 status code when swift code do not match bank branch`() {
        val swiftCode = "ABCD1234X22"

        every {
            swiftCodeService.getAllBanksBySwiftCode(swiftCode)
        } throws ResourceNotFoundException("Bank branch not found")

        mockMvc.get("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { string("Resource not found: Bank branch not found") }
        }
    }

    @Test
    fun `test that list all banks by swift code endpoint will return 200 status code when swift code matches bank branch`() {
        every {
            swiftCodeService.getAllBanksBySwiftCode(A_TEST_SWIFT_CODE_BRANCH)
        } answers {
            testBankBranchEntityA(A_TEST_SWIFT_CODE_BRANCH).toBankBranchSummaryDTO(testCountryEntityA().name)
        }

        mockMvc.get("$SWIFT_CODE_URL/$A_TEST_SWIFT_CODE_BRANCH") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("address", equalTo("Address bank branch name A")) }
            content { jsonPath("bankName", equalTo("Test bank branch name A")) }
            content { jsonPath("countryISO2", equalTo("AA")) }
            content { jsonPath("countryName", equalTo("ACOUNTRY")) }
            content { jsonPath("isHeadquarter", equalTo(false)) }
            content { jsonPath("swiftCode", equalTo("ABCD1234X22")) }
        }
    }

    @Test
    fun `test that list all banks by swift code endpoint will return 200 status code when swift code matches bank headquarter without branches`() {
        every {
            swiftCodeService.getAllBanksBySwiftCode(A_TEST_SWIFT_CODE_BRANCH)
        } answers {
            testBankEntityA(A_TEST_SWIFT_CODE_HEADQUARTER).toBankSummaryDTO(branches = emptyList(), countryName = testCountryEntityA().name)
        }

        mockMvc.get("$SWIFT_CODE_URL/$A_TEST_SWIFT_CODE_BRANCH") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("address", equalTo("Address bank name A")) }
            content { jsonPath("bankName", equalTo("Test bank headquarter name A")) }
            content { jsonPath("countryISO2", equalTo("AA")) }
            content { jsonPath("countryName", equalTo("ACOUNTRY")) }
            content { jsonPath("isHeadquarter", equalTo(true)) }
            content { jsonPath("swiftCode", equalTo("ABCD1234XXX")) }
            content { jsonPath("branches", equalTo(emptyList<SingleBankDTO>())) }
        }
    }

    @Test
    fun `test that list all banks by swift code endpoint will return 200 status code when swift code matches bank headquarter with branches`() {
        every {
            swiftCodeService.getAllBanksBySwiftCode(A_TEST_SWIFT_CODE_BRANCH)
        } answers {
            testBankEntityA(A_TEST_SWIFT_CODE_HEADQUARTER).toBankSummaryDTO(
                branches = listOf(testBankBranchEntityA(A_TEST_SWIFT_CODE_BRANCH).toSingleBankDTO()),
                countryName = testCountryEntityA().name)
        }

        mockMvc.get("$SWIFT_CODE_URL/$A_TEST_SWIFT_CODE_BRANCH") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("address", equalTo("Address bank name A")) }
            content { jsonPath("bankName", equalTo("Test bank headquarter name A")) }
            content { jsonPath("countryISO2", equalTo("AA")) }
            content { jsonPath("countryName", equalTo("ACOUNTRY")) }
            content { jsonPath("isHeadquarter", equalTo(true)) }
            content { jsonPath("swiftCode", equalTo("ABCD1234XXX")) }
            content { jsonPath("branches[0].address", equalTo("Address bank branch name A")) }
            content { jsonPath("branches[0].bankName", equalTo("Test bank branch name A")) }
            content { jsonPath("branches[0].countryISO2", equalTo("AA")) }
            content { jsonPath("branches[0].isHeadquarter", equalTo(false)) }
            content { jsonPath("branches[0].swiftCode", equalTo("ABCD1234X22")) }
        }
    }

    @Test
    fun `test that list all banks by ISO2 code endpoint will return 400 status code when ISO2 code length is incorrect`() {
        val iso2Code = "XXX"

        every {
            swiftCodeService.getAllBanksByCountryISO2Code(iso2Code)
        } throws InvalidISO2CodeException("Incorrect ISO2 code length")

        mockMvc.get("$ISO2_CODE_URL/$iso2Code") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid ISO2 code: Incorrect ISO2 code length") }
        }
    }

    @Test
    fun `test that list all banks by ISO2 code endpoint will return 400 status code when ISO2 code format is incorrect`() {
        val iso2Code = "aa"

        every {
            swiftCodeService.getAllBanksByCountryISO2Code(iso2Code)
        } throws InvalidISO2CodeException("Incorrect ISO2 code format")

        mockMvc.get("$ISO2_CODE_URL/$iso2Code") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid ISO2 code: Incorrect ISO2 code format") }
        }
    }

    @Test
    fun `test that list all banks by ISO2 code endpoint will return 404 status code when ISO2 code do not match country`() {
        val iso2Code = "AA"

        every {
            swiftCodeService.getAllBanksByCountryISO2Code(iso2Code)
        } throws ResourceNotFoundException("Country not found")

        mockMvc.get("$ISO2_CODE_URL/$iso2Code") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { string("Resource not found: Country not found") }
        }
    }

    @Test
    fun `test that list all banks by ISO2 code endpoint will return 200 status code and empty list when ISO2 code matches country without banks`() {
        val iso2Code = "AA"

        every {
            swiftCodeService.getAllBanksByCountryISO2Code(iso2Code)
        } answers {
            testCountryEntityA().toCountryISO2CodeSummaryDTO(emptyList(), emptyList())
        }

        mockMvc.get("$ISO2_CODE_URL/$iso2Code") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("countryISO2", equalTo("AA")) }
            content { jsonPath("countryName", equalTo("ACOUNTRY")) }
            content { jsonPath("swiftCodes", equalTo(emptyList<SingleBankDTO>())) }
        }
    }

    @Test
    fun `test that list all banks by ISO2 code endpoint will return 200 status code when ISO2 code matches country with banks and branches`() {
        val iso2Code = "AA"

        every {
            swiftCodeService.getAllBanksByCountryISO2Code(iso2Code)
        } answers {
            testCountryEntityA().toCountryISO2CodeSummaryDTO(
                listOf(testBankEntityA(A_TEST_SWIFT_CODE_HEADQUARTER).toSingleBankDTO()),
                listOf(testBankBranchEntityA(A_TEST_SWIFT_CODE_BRANCH).toSingleBankDTO())
            )
        }

        mockMvc.get("$ISO2_CODE_URL/$iso2Code") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("countryISO2", equalTo("AA")) }
            content { jsonPath("countryName", equalTo("ACOUNTRY")) }
            content { jsonPath("swiftCodes[0].address", equalTo("Address bank name A")) }
            content { jsonPath("swiftCodes[0].bankName", equalTo("Test bank headquarter name A")) }
            content { jsonPath("swiftCodes[0].countryISO2", equalTo("AA")) }
            content { jsonPath("swiftCodes[0].isHeadquarter", equalTo(true)) }
            content { jsonPath("swiftCodes[0].swiftCode", equalTo("ABCD1234XXX")) }
            content { jsonPath("swiftCodes[1].address", equalTo("Address bank branch name A")) }
            content { jsonPath("swiftCodes[1].bankName", equalTo("Test bank branch name A")) }
            content { jsonPath("swiftCodes[1].countryISO2", equalTo("AA")) }
            content { jsonPath("swiftCodes[1].isHeadquarter", equalTo(false)) }
            content { jsonPath("swiftCodes[1].swiftCode", equalTo("ABCD1234X22")) }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 400 status code when country ISO2 code length is incorrect`() {
        val bankDTO = testBankDTOA(countryISO2 = "XXX")

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws InvalidISO2CodeException("Incorrect ISO2 code length")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid ISO2 code: Incorrect ISO2 code length") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 400 status code when country ISO2 code format is incorrect`() {
        val bankDTO = testBankDTOA("xx")

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws InvalidISO2CodeException("Incorrect ISO2 code format")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid ISO2 code: Incorrect ISO2 code format") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 400 status code when swift code length is incorrect`() {
        val bankDTO = testBankDTOA(swiftCode = "XXX")

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws InvalidSwiftCodeException("Incorrect SWIFT code length")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Incorrect SWIFT code length") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 400 status code when swift code format is incorrect`() {
        val bankDTO = testBankDTOA(swiftCode = "XXXX1234abc")

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws InvalidSwiftCodeException("Incorrect SWIFT code format")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Incorrect SWIFT code format") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 400 status code when swift code is branch and ends with XXX`() {
        val bankDTO = testBankBranchDTOA(swiftCode = "ABCD1234XXX")

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws InvalidSwiftCodeException("Headquarter bank should end with XXX")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Headquarter bank should end with XXX") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 404 status code when headquarter bank not found for branch bank`() {
        val bankDTO = testBankBranchDTOA()

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws ResourceNotFoundException("Headquarter bank not found")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isNotFound() }
            content { string("Resource not found: Headquarter bank not found") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 400 status code when bank already exists`() {
        val bankDTO = testBankBranchDTOA()

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } throws InvalidSwiftCodeException("Bank already exists")

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Bank already exists") }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 200 status code when bank is headquarter`() {
        val bankDTO = testBankHeadquarterDTOA()

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } answers {
            "Bank headquarter with Swift code ${bankDTO.swiftCode} has been added to the system"
        }

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `test that add bank to swift endpoint will return 200 status code when bank is branch`() {
        val bankDTO = testBankBranchDTOA()

        every {
            swiftCodeService.addBankToSwift(bankDTO)
        } answers {
            "Bank branch with Swift code ${bankDTO.swiftCode} has been added to the system"
        }

        mockMvc.post(SWIFT_CODE_URL) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bankDTO)
        }.andExpect {
            status { isOk() }
            content { string("Bank branch with Swift code ${bankDTO.swiftCode} has been added to the system") }
        }
    }

    @Test
    fun `test that delete bank endpoint will return 400 status code when swift code length is incorrect`() {
        val swiftCode = "XXX"

        every {
            swiftCodeService.deleteBank(swiftCode)
        } throws InvalidSwiftCodeException("Incorrect SWIFT code length")

        mockMvc.delete("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Incorrect SWIFT code length") }
        }
    }

    @Test
    fun `test that delete bank endpoint will return 400 status code when swift code format is incorrect`() {
        val swiftCode = "XXXX1234abc"

        every {
            swiftCodeService.deleteBank(swiftCode)
        } throws InvalidSwiftCodeException("Incorrect SWIFT code format")

        mockMvc.delete("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { string("Invalid SWIFT code: Incorrect SWIFT code format") }
        }
    }

    @Test
    fun `test that delete bank endpoint will return 404 status code when bank headquarter not found`() {
        val swiftCode = "ABCD1234XXX"

        every {
            swiftCodeService.deleteBank(swiftCode)
        } throws ResourceNotFoundException("Bank headquarter not found")

        mockMvc.delete("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { string("Resource not found: Bank headquarter not found") }
        }
    }

    @Test
    fun `test that delete bank endpoint will return 404 status code when bank branch not found`() {
        val swiftCode = "ABCD1234X22"

        every {
            swiftCodeService.deleteBank(swiftCode)
        } throws ResourceNotFoundException("Bank branch not found")

        mockMvc.delete("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { string("Resource not found: Bank branch not found") }
        }
    }

    @Test
    fun `test that delete bank endpoint will return 200 status code when bank headquarter is deleted`() {
        val swiftCode = "ABCD1234XXX"

        every {
            swiftCodeService.deleteBank(swiftCode)
        } answers {
            "Bank headquarter with Swift code $swiftCode has been deleted from the system and it's branches"
        }

        mockMvc.delete("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { string("Bank headquarter with Swift code $swiftCode has been deleted from the system and it's branches") }
        }
    }

    @Test
    fun `test that delete bank endpoint will return 200 status code when bank branch is deleted`() {
        val swiftCode = "ABCD1234X22"

        every {
            swiftCodeService.deleteBank(swiftCode)
        } answers {
            "Bank branch with Swift code $swiftCode has been deleted from the system"
        }

        mockMvc.delete("$SWIFT_CODE_URL/$swiftCode") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { string("Bank branch with Swift code $swiftCode has been deleted from the system") }
        }
    }
}