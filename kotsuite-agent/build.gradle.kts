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

    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
}

tasks.jar {
    manifest {
        attributes(
            "Premain-Class" to "org.kotsuite.agent.Main",
            "Agent-Class" to "org.kotsuite.agent.Main",
            "Can-Redefine-Classes" to "true",
            "Can-Retransfrom-Classes" to "true"
        )
    }
}