plugins {
    kotlin("jvm")
}

group = "org.kotsuite"
version = "1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.5")
    implementation("org.ow2.asm:asm-commons:9.5")
    implementation("org.ow2.asm:asm-util:9.5")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

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