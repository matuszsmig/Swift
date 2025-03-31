package com.example.swift.adapters.repositories

import com.example.swift.domain.model.entities.CountryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository: JpaRepository<CountryEntity, String>