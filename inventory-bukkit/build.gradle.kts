import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":inventory-api"))
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

task("sourcesJar", type = Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveFileName.set("${project.name}-${project.version}.jar")
        mergeServiceFiles()
        exclude("kotlin/**")
    }
}