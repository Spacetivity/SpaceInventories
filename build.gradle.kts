import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"
}

allprojects {
    group = "world.neptuns.inventory"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://nexus.neptuns.world/repository/maven-public/")
            credentials {
                username = property("nexusUsername") as String
                password = property("nexusPassword") as String
            }
        }
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    afterEvaluate {
        dependencies {
            compileOnly(libs.gson)
            compileOnly(libs.paper.api)
        }
    }
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = libs.versions.java.get()
}