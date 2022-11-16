val ktorVersion: String by project
val koinVersion: String by project
val kotlinVersion: String by project
val exposedVersion: String by project
val junitVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("com.adarshr.test-logger") version "3.2.0"
    id("org.jmailen.kotlinter") version "3.12.0"
}

apply(plugin = "com.karumi.kotlin-snapshot")

buildscript {
    repositories {
        mavenCentral()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.karumi.kotlinsnapshot:plugin:2.2.3")
    }
}

version = "0.2.0"
group = "com.example"
val projectName = "exposed.transaction"

application {
    mainClass.set("$group.$projectName.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")

    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.5.0")

    implementation("ch.qos.logback:logback-classic:1.4.4")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
