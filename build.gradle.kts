import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mergedJar by configurations.creating<Configuration> {
    // We're going to resolve this config here, in this project
    isCanBeResolved = true

    // This configuration will not be consumed by other projects
    isCanBeConsumed = false

    // Don't make this visible to other projects
    isVisible = false
}

plugins {
    kotlin("jvm") version "1.8.10"
    application
}

group = "com.maples"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations.all {
    exclude("org.slf4j")
}

dependencies {
    mergedJar(project(":kotsuite-analyzer"))
    mergedJar(project(":kotsuite-client"))
    mergedJar(project(":kotsuite-ga"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


tasks.jar {
    dependsOn(mergedJar)

    from({
        mergedJar.filter {
            it.name.endsWith("jar") && it.path.contains(rootDir.path)
        }.map {
            logger.lifecycle("depending on $it")
            zipTree(it)
        }
    })
}

