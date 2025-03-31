package com.example.swift.adapters.services

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

@Configuration
class DbInitializer {

    @Bean
    fun runScript(): ApplicationRunner {
        return ApplicationRunner {
            try {
                val process = ProcessBuilder("python", "script.py").start()
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val errorReader = BufferedReader(InputStreamReader(process.errorStream))
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    println(line)
                }

                while (errorReader.readLine().also { line = it } != null) {
                    System.err.println(line)
                }

                process.waitFor()
                println("Database population script executed successfully")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}