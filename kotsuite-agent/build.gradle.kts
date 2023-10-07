plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.0.0"
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

    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
}

tasks {
    jar {
        manifest {
            attributes(
                "Premain-Class" to "org.kotsuite.agent.Main",
                "Agent-Class" to "org.kotsuite.agent.Main",
                "Can-Redefine-Classes" to "true",
                "Can-Retransfrom-Classes" to "true"
            )
        }
    }

    shadowJar {
        archiveBaseName.set("kotsuite-agent-shadow")

        manifest {
            attributes(
                "Premain-Class" to "org.kotsuite.agent.Main",
                "Agent-Class" to "org.kotsuite.agent.Main",
                "Can-Redefine-Classes" to "true",
                "Can-Retransfrom-Classes" to "true"
            )
        }
    }

    val copyJarToTarget by creating(Copy::class.java) {
        from(shadowJar)
        into(file("/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs/cli/"))
    }

    build {
        dependsOn(shadowJar)
        dependsOn(copyJarToTarget)
    }
}
