plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
}

group = "ru.lewis.fragments.integration"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Подключаем API так, как это сделал бы сторонний плагин: compileOnly.
    compileOnly(project(":Api"))
    compileOnly(libs.paper.api)
}

kotlin {
    jvmToolchain(21)
}
