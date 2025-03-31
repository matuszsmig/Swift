package com.example.swift.adapters.repositories

import com.example.swift.domain.model.entities.BankBranchEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BankBranchRepository: JpaRepository<BankBranchEntity, String>