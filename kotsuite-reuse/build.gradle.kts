plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":kotsuite-common"))
    implementation(project(":kotsuite-analyzer"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}