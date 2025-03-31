package com.example.swift.exceptions

import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(InvalidSwiftCodeException::class)
    fun handleInvalidSwiftCode(ex: InvalidSwiftCodeException): ResponseEntity<String> {
        return ResponseEntity("Invalid SWIFT code: ${ex.message}", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidISO2CodeException::class)
    fun handleInvalidISO2Code(ex: InvalidISO2CodeException): ResponseEntity<String> {
        return ResponseEntity("Invalid ISO2 code: ${ex.message}", HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<String> {
        return ResponseEntity("Resource not found: ${ex.message}", HttpStatus.NOT_FOUND)
    }
}