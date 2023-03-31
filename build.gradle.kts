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
    id("com.github.johnrengelman.shadow") version "7.0.0"
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

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val kotlinRuntime = setOf("kotlin-stdlib-1.8.10.jar", "kotlin-stdlib-1.8.10.jar")
    val subprojects = setOf("kotsuite-client", "kotsuite-analyzer", "kotsuite-ga", "kotsuite-reuse")

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "org.kotsuite.client.MainKt"
        attributes["Class-Path"] = "libs/"
    }

    from({
        mergedJar.filter {
            it.name.endsWith("jar") && it.path.contains(rootDir.path)
        }.map {
            logger.lifecycle("depending on $it")
            zipTree(it)
        }
    })

    from(
        configurations.runtimeClasspath.get()
            .filter { it.name in kotlinRuntime }
            .map { zipTree(it) }
            .also { from(it) }
    )

    from(
        subprojects.map { project ->
            project(":$project").configurations.runtimeClasspath.get()
                .map {
                    if (it.isDirectory) it else zipTree(it)
                }
        }
    )

//    from(
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    )
}

tasks.shadowJar {
    archiveBaseName.set("shadow-kotsuite")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "org.kotsuite.client.MainKt"
    }

    from(
        mergedJar.filter {
            it.name.endsWith("jar")
        }.map {
            logger.lifecycle("depending on $it")
            zipTree(it)
        }
    )

//    mergeServiceFiles()
//    minimize()
//
//    configurations.forEach { configuration ->
//        from(configuration)
//    }
}