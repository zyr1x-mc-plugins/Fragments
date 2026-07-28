import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "2.2.21"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "ru.lewis.fragments"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.xenondevs.xyz/releases/")
    maven("https://repo.panda-lang.org/releases")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // CORE
    compileOnly(libs.paper.api)

    // KOTLIN RUNTIME (нужно резолвить как library, иначе Intrinsics не найдётся на рантайме)
    library(kotlin("stdlib"))

    // TOOLS
    library(libs.guice)
    library(libs.hibernate.core)
    library(libs.hibernate.hikari)
    library(dependencyNotation = libs.redisson)
    library(libs.kryo)
    library(libs.mariadb)
    library(libs.duration.serializer)

    // MINECRAFT TOOLS
    compileOnly(project(":Api"))
    compileOnly(libs.placeholderapi)
    compileOnly(libs.kyori.minimessage)
    compileOnly(libs.invui)
    compileOnly(libs.worldedit)
    compileOnly(libs.worldguard)
    library(libs.litecommands)
    library(libs.sponge.yaml)
    library(libs.sponge.extra.kotlin)
    compileOnly(files("gradle/libs/FancyNpcs-2.11.0.jar"))
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.gson:gson:2.11.0")
    }
}

paper {
    name = "Fragments"
    version = "1.0"
    main = "ru.lewis.fragments.bootstrap.Bootstrap"
    loader = "ru.lewis.fragments.FragmentsLoader"
    apiVersion = "1.21"
    author = "Lewis Carrol"

    generateLibrariesJson = true

    serverDependencies {
        register("WorldEdit") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("WorldGuard") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("FancyNpcs") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

kotlin {
    jvmToolchain(21)
}