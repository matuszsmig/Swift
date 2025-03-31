# Swift

## Overview
This project is a Spring Boot application that manages bank by SWIFT codes. It provides RESTful APIs to interact with the data.

## General
- I decided to use a PostgreSQL database and structured it into three tables: `countries`, `banks`, and `bank_branches` to ensure fast, low-latency access to the data.
- I omitted the `Code Type` and `Town Name` columns, as they are redundant and not used in the responses.
- I made the following assumption about existing banks: branches cannot exist without their headquarters, and deleting a headquarters will also remove all its branches.
- I provided a Python script to populate the database with data from CSV files.

## Database Schema
The database consists of the following tables:

- **countries**
    - `iso2_code` (Primary Key)
    - `name`
    - `time_zone`

- **banks**
    - `swift_code` (Primary Key)
    - `name`
    - `address`
    - `is_headquarter`
    - `country_iso2_code` (Foreign Key referencing `countries.iso2_code`)

- **bank_branches**
    - `swift_code` (Primary Key)
    - `name`
    - `address`
    - `is_headquarter`
    - `country_iso2_code` (Foreign Key referencing `countries.iso2_code`)
    - `headquarters_swift_code` (Foreign Key referencing `banks.swift_code`)

## Technologies Used
- Kotlin
- Spring Boot
- JPA/Hibernate
- H2 Database (for testing)
- Gradle
- Flyway (for database migrations)
- Postgres (for production)
- Docker
- Swagger (for API testing)

## Running the Application
To run the application, use the following command:

```sh
docker-compose up --build
```

## Running Tests
To run all tests from the terminal, use the following command:

```sh
./gradlew test