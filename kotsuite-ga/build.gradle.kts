plugins {
    kotlin("jvm")
}

group = "com.maples"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kotsuite-reuse"))

    testImplementation("org.jacoco:org.jacoco.agent:0.8.8")
    implementation("org.jacoco:org.jacoco.core:0.8.8")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}