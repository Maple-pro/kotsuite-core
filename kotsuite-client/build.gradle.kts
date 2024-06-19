plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotsuite-common"))
    implementation(project(":kotsuite-analyzer"))
    implementation(project(":kotsuite-ga"))

    implementation("org.soot-oss:soot:4.4.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    implementation("commons-cli:commons-cli:1.5.0")
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<Jar> {
        manifest {
            attributes["Main-Class"] = "org.kotsuite.client.Main"
        }
    }
}
