plugins {
    kotlin("jvm")
}

group = "org.kotsuite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kotsuite-analyzer"))
    implementation(project(":kotsuite-ga"))

    implementation("org.soot-oss:soot:4.4.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation("org.slf4j:slf4j-simple:2.0.6")

    implementation("commons-cli:commons-cli:1.5.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "org.kotsuite.client.Main"
    }
}