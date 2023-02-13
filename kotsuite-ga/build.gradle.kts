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

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    testImplementation(kotlin("test"))

    testImplementation("org.jacoco:org.jacoco.agent:0.8.8")

    implementation("org.jacoco:org.jacoco.core:0.8.8")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}