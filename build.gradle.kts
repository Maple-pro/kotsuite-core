import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.date
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
    `maven-publish`
    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "2.0.0"
}

group = "org.kotsuite"
version = "1.1.4"

repositories {
    mavenCentral()
}

configurations.all {
    exclude("org.slf4j")
}

changelog {
    version.set("1.1.4")
    path.set(file("CHANGELOG.md").canonicalPath)
    header.set(provider { "[${version.get()}] - ${date()}" })
    headerParserRegex.set("""(\d+\.\d+\.\d+)""".toRegex())
    introduction.set(
        """
        My awesome project that provides a lot of useful features, like:
        
        - Feature 1
        - Feature 2
        - and Feature 3
        """.trimIndent()
    )
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
    lineSeparator.set("\n")
    combinePreReleases.set(true)
    sectionUrlBuilder.set(
        ChangelogSectionUrlBuilder { repositoryUrl, currentVersion, previousVersion, isUnreleased ->
            "foo"
        }
    )
}

dependencies {
    mergedJar(project(":kotsuite-analyzer"))
    mergedJar(project(":kotsuite-client"))
    mergedJar(project(":kotsuite-ga"))
    mergedJar(project(":kotsuite-common"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.jar {
    dependsOn(mergedJar)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "org.kotsuite.client.MainKt"
//        attributes["Class-Path"] = "libs/"
    }

    from({
        mergedJar.filter {
            it.name.endsWith("jar") && it.path.contains(rootDir.path)
        }.map {
            logger.lifecycle("depending on $it")
            zipTree(it)
        }
    })
}

tasks.register("fatJar", Jar::class.java) {
    archiveBaseName.set("kotsuite-core-fat")

    dependsOn(mergedJar)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val kotlinRuntime = setOf("kotlin-stdlib-1.8.10.jar", "kotlin-stdlib-1.8.10.jar")
    val subprojects = setOf("kotsuite-client", "kotsuite-analyzer", "kotsuite-ga", "kotsuite-reuse")

    manifest {
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "org.kotsuite.client.MainKt"
        attributes["Multi-Release"] = "true"
//        attributes["Class-Path"] = "libs/"
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
            project(":$project").configurations.testCompileClasspath.get()
                .map {
                    if (it.isDirectory) it else zipTree(it)
                }
        }
    )
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

tasks.build {
    dependsOn(tasks["fatJar"])
}

publishing {

    repositories {
        maven {
            name = "localRepo"
            url = uri("$buildDir/repo")
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")

    publishing {
        repositories {
            maven {
                name = "localRepo"
                url = uri("$buildDir/repo")
            }
        }

        publications {
            create<MavenPublication>("maven") {
//                from(components["java"])
                afterEvaluate {
                    artifactId = tasks.jar.get().archiveBaseName.get()
                }
            }
        }
    }
}