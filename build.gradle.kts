import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.17199"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations.all {
    exclude("org.slf4j")
}

dependencies {
    // https://mvnrepository.com/artifact/log4j/log4j
//    implementation("log4j:log4j:1.2.17")

    // https://mvnrepository.com/artifact/org.slf4j/slf4j-log4j12
//    testImplementation("org.slf4j:slf4j-log4j12:2.0.6")

    testImplementation(kotlin("test"))

    // https://mvnrepository.com/artifact/org.jacoco/org.jacoco.agent
    testImplementation("org.jacoco:org.jacoco.agent:0.8.8")

    // https://mvnrepository.com/artifact/org.jacoco/org.jacoco.core
    implementation("org.jacoco:org.jacoco.core:0.8.8")

    // https://mvnrepository.com/artifact/org.soot-oss/soot
    implementation("org.soot-oss:soot:4.3.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.core
    implementation("org.soot-oss:sootup.core:1.0.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.java.core
    implementation("org.soot-oss:sootup.java.core:1.0.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.java.sourcecode
    implementation("org.soot-oss:sootup.java.sourcecode:1.0.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.java.bytecode
    implementation("org.soot-oss:sootup.java.bytecode:1.0.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.jimple.parser
    implementation("org.soot-oss:sootup.jimple.parser:1.0.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.callgraph
    implementation("org.soot-oss:sootup.callgraph:1.0.0")
    // https://mvnrepository.com/artifact/org.soot-oss/sootup.analysis
    implementation("org.soot-oss:sootup.analysis:1.0.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}