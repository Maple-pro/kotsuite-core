plugins {
    kotlin("jvm")
}

group = "org.kotsuite"
version = "1.1.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kotsuite-common"))
    implementation(project(":kotsuite-analyzer"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}