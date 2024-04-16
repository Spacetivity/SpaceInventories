plugins {
    kotlin("jvm") version "1.9.23"
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
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
                "https://nexus.neptuns.world/repository/maven-snapshots/"
            } else {
                "https://nexus.neptuns.world/repository/maven-releases/"
            }

            url = uri(repositoryUrl)

            credentials {
                username = property("nexusUsername") as String
                password = property("nexusPassword") as String
            }
        }
    }
}