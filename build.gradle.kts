import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.diffplug.spotless") version "6.15.0"
    application
}

group = "com.maples"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations.all {
    exclude("org.slf4j")
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}