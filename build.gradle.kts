import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"
}

allprojects {
    group = "net.spacetivity.inventory"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        mavenCentral()
    }

    apply(plugin = "java")
    apply(plugin = "maven-publish")

    afterEvaluate {
        dependencies {
            compileOnly(libs.gson)
            compileOnly(libs.api.paper)
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