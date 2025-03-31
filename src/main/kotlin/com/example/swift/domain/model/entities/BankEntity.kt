package com.example.swift.domain.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "banks")
data class BankEntity(
    @Id
    @Column(name = "swift_code")
    val swiftCode: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "address")
    val address: String? = null,

    @Column(name = "is_headquarter")
    val isHeadquarter: Boolean = true,

    @ManyToOne
    @JoinColumn(name = "country_iso2_code", referencedColumnName = "iso2_code")
    val countryIso2Code: CountryEntity,

    @OneToMany(mappedBy = "headquartersSwiftCode", cascade = [CascadeType.REMOVE])
    val bankBranchesEntities: List<BankBranchEntity> = emptyList()
) {
    constructor() : this("", "", "", true, CountryEntity())
}