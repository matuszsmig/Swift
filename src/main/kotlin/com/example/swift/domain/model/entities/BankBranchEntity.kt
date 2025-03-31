package com.example.swift.domain.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "bank_branches")
data class BankBranchEntity(
    @Id
    @Column(name = "swift_code")
    val swiftCode: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "address")
    val address: String? = null,

    @Column(name = "is_headquarter")
    val isHeadquarter: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "country_iso2_code", referencedColumnName = "iso2_code")
    val countryIso2Code: CountryEntity,

    @ManyToOne
    @JoinColumn(name = "headquarters_swift_code", referencedColumnName = "swift_code")
    val headquartersSwiftCode: BankEntity,
) {
    constructor() : this("", "", "", false, CountryEntity(), BankEntity())
}