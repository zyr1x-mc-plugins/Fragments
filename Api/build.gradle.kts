plugins {
    id("java")
    `maven-publish`
}

group = "ru.lewis.fragments.api"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"

            url = uri(
                "https://maven.pkg.github.com/zyr1x-mc-plugins/Fragments"
            )

            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])

            groupId = "ru.lewis.fragments"
            artifactId = "api"
            version = "1.0.1"
        }
    }
}