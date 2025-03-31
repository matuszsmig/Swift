package com.example.swift.adapters.services

import com.example.swift.*
import com.example.swift.adapters.repositories.BankBranchRepository
import com.example.swift.adapters.repositories.BankRepository
import com.example.swift.adapters.repositories.CountryRepository
import com.example.swift.adapters.services.impl.SwiftCodeServiceImpl
import com.example.swift.exceptions.InvalidISO2CodeException
import com.example.swift.exceptions.InvalidSwiftCodeException
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import kotlin.test.Test

@SpringBootTest
@Transactional
class SwiftCodeServiceImplTest @Autowired constructor(
    private val underTest: SwiftCodeServiceImpl,
    private val bankBranchRepository: BankBranchRepository,
    private val bankRepository: BankRepository,
    private val countryRepository: CountryRepository
) {

    @Test
    fun `test that getAllBanksBySwiftCode throws exception when swift code is incorrect length`() {
        assertThrows<InvalidSwiftCodeException> {
            underTest.getAllBanksBySwiftCode("ABCD1234")
        }
    }

    @Test
    fun `test that getAllBanksBySwiftCode throws exception when swift code is incorrect format`() {
        assertThrows<InvalidSwiftCodeException> {
            underTest.getAllBanksBySwiftCode("ABCD1234xxx")
        }
    }

    @Test
    fun `test that getAllBanksBySwiftCode throws exception when bank headquarter is not found`() {
        assertThrows<ResourceNotFoundException> {
            underTest.getAllBanksBySwiftCode("ABCD1234XXX")
        }
    }

    @Test
    fun `test that getAllBanksBySwiftCode throws exception when bank branch is not found`() {
        assertThrows<ResourceNotFoundException> {
            underTest.getAllBanksBySwiftCode("ABCD1234ABC")
        }
    }

    @Test
    fun `test that getAllBanksBySwiftCode returns bank headquarter when it is found`() {
        countryRepository.save(testCountryEntityA())
        val bank = bankRepository.save(testBankEntityA())

        val result = underTest.getAllBanksBySwiftCode(bank.swiftCode)
        assertThat(result).isEqualTo(bank.toBankSummaryDTO(emptyList(), bank.countryIso2Code.name))
    }

    @Test
    fun `test that getAllBanksBySwiftCode returns bank branch when it is found`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())
        val bank = bankBranchRepository.save(testBankBranchEntityA())

        val result = underTest.getAllBanksBySwiftCode(bank.swiftCode)
        assertThat(result).isEqualTo(bank.toBankBranchSummaryDTO(bank.countryIso2Code.name))
    }

    @Test
    fun `test that getAllBanksByCountryISO2Code throws exception when iso2 code is incorrect length`() {
        assertThrows<InvalidISO2CodeException> {
            underTest.getAllBanksByCountryISO2Code("A")
        }
    }

    @Test
    fun `test that getAllBanksByCountryISO2Code throws exception when iso2 code is incorrect format`() {
        assertThrows<InvalidISO2CodeException> {
            underTest.getAllBanksByCountryISO2Code("aA")
        }
    }

    @Test
    fun `test that getAllBanksByCountryISO2Code throws exception when country is not found`() {
        assertThrows<ResourceNotFoundException> {
            underTest.getAllBanksByCountryISO2Code("AA")
        }
    }

    @Test
    fun `test that getAllBanksByCountryISO2Code returns country with empty list when it is found but banks are assigned to this country`() {
        val country = countryRepository.save(testCountryEntityA())

        val result = underTest.getAllBanksByCountryISO2Code(country.iso2Code)
        assertThat(result).isEqualTo(country.toCountryISO2CodeSummaryDTO(emptyList(), emptyList()))
    }

    @Test
    fun `test that addBankToSwift throws exception when country is not found`() {
        assertThrows<ResourceNotFoundException> {
            underTest.addBankToSwift(testBankDTOA())
        }
    }

    @Test
    fun `test that addBankToSwift throws exception when bank already exists`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())

        assertThrows<InvalidSwiftCodeException> {
            underTest.addBankToSwift(testBankDTOA(swiftCode = "ABCD1234XXX"))
        }
    }

    @Test
    fun `test that addBankToSwift successfully adds bank headquarter to the system`() {
        countryRepository.save(testCountryEntityA())
        val result = underTest.addBankToSwift(testBankHeadquarterDTOA())

        assertThat(result).isEqualTo("Bank headquarter with Swift code ABCD1234XXX has been added to the system")
        assertThat(bankRepository.findByIdOrNull("ABCD1234XXX")).isNotNull()
    }

    @Test
    fun `test that addBankToSwift successfully adds bank branch to the system`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())
        val result = underTest.addBankToSwift(testBankBranchDTOA())

        assertThat(result).isEqualTo("Bank branch with Swift code ABCD1234X22 has been added to the system")
        assertThat(bankBranchRepository.findByIdOrNull("ABCD1234X22")).isNotNull()
    }

    @Test
    fun `test that addBankToSwift throws exception when bank branch already exists`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())
        bankBranchRepository.save(testBankBranchEntityA())

        assertThrows<InvalidSwiftCodeException> {
            underTest.addBankToSwift(testBankBranchDTOA())
        }
    }

    @Test
    fun `test that addBankToSwift throws exception when bank headquarter already exists`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())

        assertThrows<InvalidSwiftCodeException> {
            underTest.addBankToSwift(testBankHeadquarterDTOA())
        }
    }

    @Test
    fun `test that deleteBank throws exception when swift code is incorrect length`() {
        assertThrows<InvalidSwiftCodeException> {
            underTest.deleteBank("ABCD1234")
        }
    }

    @Test
    fun `test that deleteBank throws exception when swift code is incorrect format`() {
        assertThrows<InvalidSwiftCodeException> {
            underTest.deleteBank("ABCD1234xxx")
        }
    }

    @Test
    fun `test that deleteBank throws exception when bank headquarter is not found`() {
        assertThrows<ResourceNotFoundException> {
            underTest.deleteBank("ABCD1234XXX")
        }
    }

    @Test
    fun `test that deleteBank throws exception when bank branch is not found`() {
        assertThrows<ResourceNotFoundException> {
            underTest.deleteBank("ABCD1234ABC")
        }
    }

    @Test
    fun `test that deleteBank successfully deletes bank headquarter from the system`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())

        val result = underTest.deleteBank("ABCD1234XXX")
        assertThat(result).isEqualTo("Bank headquarter with Swift code ABCD1234XXX has been deleted from the system and it's branches")
        assertThat(bankRepository.findByIdOrNull("ABCD1234XXX")).isNull()
    }

    @Test
    fun `test that deleteBank successfully deletes bank branch from the system`() {
        countryRepository.save(testCountryEntityA())
        bankRepository.save(testBankEntityA())
        bankBranchRepository.save(testBankBranchEntityA())

        val result = underTest.deleteBank("ABCD1234X22")
        assertThat(result).isEqualTo("Bank branch with Swift code ABCD1234X22 has been deleted from the system")
        assertThat(bankBranchRepository.findByIdOrNull("ABCD1234X22")).isNull()
    }
}