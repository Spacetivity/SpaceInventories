import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
}

dependencies {

}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    archiveBaseName.set("SpaceInventoryApi")
    from({ configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) } })
}

task("sourcesJar", type = Jar::class) {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
        }
    }

    repositories {
        maven {
            val repositoryUrl = if (project.version.toString().endsWith("SNAPSHOT")) {
                "https://nexus.spacetivity.net/repository/maven-snapshots/"
            } else {
                "https://nexus.spacetivity.net/repository/maven-releases/"
            }

            url = uri(repositoryUrl)

            credentials {
                username = property("nexusUsername") as String
                password = property("nexusPassword") as String
            }
        }
    }
}