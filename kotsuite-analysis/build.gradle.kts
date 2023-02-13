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

    // https://mvnrepository.com/artifact/org.soot-oss/soot
    implementation("org.soot-oss:soot:4.4.1")
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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}