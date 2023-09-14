import java.nio.file.Files

plugins {
    kotlin("jvm")
}

group = "org.kotsuite"
version = "1.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.soot-oss:soot:4.4.1")

//    implementation("org.soot-oss:sootup.core:1.0.0")
//    implementation("org.soot-oss:sootup.java.core:1.0.0")
//    implementation("org.soot-oss:sootup.java.sourcecode:1.0.0")
//    implementation("org.soot-oss:sootup.java.bytecode:1.0.0")
//    implementation("org.soot-oss:sootup.jimple.parser:1.0.0")
//    implementation("org.soot-oss:sootup.callgraph:1.0.0")
//    implementation("org.soot-oss:sootup.analysis:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.register("compileTargetsResources") {
    val directory = file("./src/test/targets-resources")

    doLast {
        Files.createDirectories(directory.toPath())
        fileTree("./src/test/targets-resources").map { println(it.path) }
    }
}