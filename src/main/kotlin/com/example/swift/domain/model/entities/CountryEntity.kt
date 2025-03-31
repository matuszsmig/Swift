package com.example.swift.domain.model.entities

import jakarta.persistence.*

@Entity
@Table(name = "countries")
data class CountryEntity(
    @Id
    @Column(name = "iso2_code")
    val iso2Code: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "time_zone")
    val timeZone: String,

    @OneToMany(mappedBy = "countryIso2Code", cascade = [CascadeType.REMOVE])
    val bankEntities: List<BankEntity> = emptyList(),

    @OneToMany(mappedBy = "countryIso2Code", cascade = [CascadeType.REMOVE])
    val bankBranchesEntities: List<BankBranchEntity> = emptyList()
) {
    constructor() : this("", "", "")
}