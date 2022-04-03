plugins {
    `kotlin-dsl`
    id("com.github.eugenesy.scapegoat") version "0.2.0"
    id("org.scoverage") version "7.0.0"
    id("cz.alenkacz.gradle.scalafmt") version "1.16.2"
}

repositories {
    gradlePluginPortal()
}

tasks {
    reportScoverage
}

