group = "xyz.sirblobman.alienware"
version = "1.0-SNAPSHOT"

plugins {
    id("java")
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:26.0.2-1") // JetBrains Annotations
    implementation("info.picocli:picocli:4.7.7") // PicoCLI
    implementation("net.codecrete.usb:java-does-usb:1.2.1") // Java Does USB
    implementation("com.google.code.gson:gson:2.13.2") // Google GSON
}

tasks {
    named<Jar>("jar") {
        manifest {
            val manifestDependencies = configurations.runtimeClasspath.get().joinToString(" ") { it.name }
            attributes["Main-Class"] = "xyz.sirblobman.alienware.application.Main"
            attributes["Class-Path"] = manifestDependencies
        }
    }

    // build.gradle.kts
    register<Copy>("copyDependenciesToLibs") {
        from(configurations.runtimeClasspath)
        into("${layout.buildDirectory.get()}/libs")
    }

    named("build") {
        dependsOn("copyDependenciesToLibs")
    }
}