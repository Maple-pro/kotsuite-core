plugins {
    kotlin("jvm")
}

group = "org.kotsuite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kotsuite-reuse"))
    implementation(project(":kotsuite-analyzer"))

    implementation("org.soot-oss:soot:4.4.1")

    testImplementation("org.jacoco:org.jacoco.agent:0.8.8")
    implementation("org.jacoco:org.jacoco.core:0.8.8")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("io.mockk:mockk:1.13.5")

    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
