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
    implementation("net.codecrete.usb:java-does-usb:1.2.1")
}