plugins {
    kotlin("jvm")
}

group = "com.maples"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

//    implementation()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}