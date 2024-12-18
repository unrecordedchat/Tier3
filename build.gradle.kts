/*
 * VIA University College - School of Technology and Business
 * Software Engineering Program - 3rd Semester Project
 *
 * This work is a part of the academic curriculum for the Software Engineering program at VIA University College.
 * It is intended only for educational and academic purposes.
 *
 * No part of this project may be reproduced or transmitted in any form or by any means,
 * except as permitted by VIA University and the course instructor.
 * All rights reserved by the contributors and VIA University College.
 *
 * Project Name: Unrecorded
 * Author: Sergiu Chirap
 * Year: 2024
 */

// Define versions of dependencies used in the project for easy updates and consistency
val ktVersion: String = "1.9.10" // Kotlin version
val ktorVersion: String = "2.3.4" // Ktor framework version
val kotlinxSerializationVersion: String = "1.5.1" // KotlinX Serialization library version
val koinVersion: String = "3.5.0" // Koin dependency injection library version
val logbackVersion: String = "1.4.12" // Logback library for logging
val postgresVersion: String = "42.7.4" // PostgreSQL JDBC driver version
val hibernateVersion: String = "6.6.3.Final" // Hibernate ORM version
val argon2Version: String = "2.11" // Argon2 password hashing library version

// Specify the Java toolchain configuration
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Use Java 17 as the language version
    }
}

// Declare plugins required for the project
plugins {
    kotlin("jvm") version "1.9.10" // JVM target for Kotlin
    kotlin("plugin.serialization") version "1.9.10" // Serialization support for Kotlin
    application // Enables application-specific configuration
}

// Set group ID and version for the application
group = "com.unrecorded" // Group ID for the project
version = "0.4" // Version of the project

// Application-specific configuration
application {
    mainClass.set("io.ktor.server.netty.EngineMain") // Main class for starting the Ktor server (uses Netty)
    val isDevelopment: Boolean = project.ext.has("development") // Check for the "development" environment flag
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment") // JVM arguments for the environment
}

// Configure Maven Central as the repository for dependencies
repositories {
    mavenCentral() // Use Maven Central repository to fetch dependencies
}

// Declare dependencies for the project
dependencies {
    // Ktor core and essential modules
    implementation("io.ktor:ktor-server-core:$ktorVersion") // Ktor server core
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion") // Ktor status pages feature for error handling
    implementation("io.ktor:ktor-network-tls-jvm:$ktorVersion") // Network TLS support for secure connections
    implementation("io.ktor:ktor-server-netty:$ktorVersion") // Netty engine for Ktor

    // Configuration and content negotiation
    implementation("io.ktor:ktor-server-config-yaml:$ktorVersion") // Support for YAML-based configuration
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion") // Content negotiation (e.g., JSON, XML)

    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion") // JSON serialization with KotlinX library
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion") // JSON support for KotlinX Serialization

    // Logging
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion") // Request/response logging for Ktor servers
    implementation("ch.qos.logback:logback-classic:$logbackVersion") // Logback configuration for logging

    // Dependency Injection with Koin
    implementation("io.insert-koin:koin-ktor:$koinVersion") // Koin integration with Ktor
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion") // SLF4J logger bridge for Koin

    // Database-related dependencies
    implementation("org.postgresql:postgresql:$postgresVersion") // PostgreSQL database driver
    implementation("org.hibernate.orm:hibernate-core:$hibernateVersion") // Hibernate ORM library
    implementation("jakarta.transaction:jakarta.transaction-api") // Jakarta Transaction API for database transaction management

    // Security-related dependencies
    implementation("de.mkammerer:argon2-jvm:$argon2Version") // Argon2 library for password hashing

    // Testing dependencies
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion") // Test host environment for Ktor servers
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$ktVersion") // JUnit integration for Kotlin tests
}