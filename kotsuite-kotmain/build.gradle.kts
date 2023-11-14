plugins {
    id("java")
}

group = "org.kotsuite"
version = "1.1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("junit:junit:4.13.2")
    implementation("commons-cli:commons-cli:1.5.0")
}

tasks {
    val copyKotMainClassFileToTarget by creating(Copy::class.java) {
        from(file("/home/yangfeng/Repos/kotsuite-project/kotsuite-core/kotsuite-kotmain/build/classes/java/main/KotMain.class"))
        into(file("/home/yangfeng/Repos/kotsuite-project/kotsuite-core/libs/cli/"))
    }

    classes {
        dependsOn(copyKotMainClassFileToTarget)
    }

    test {
        useJUnitPlatform()
    }
}