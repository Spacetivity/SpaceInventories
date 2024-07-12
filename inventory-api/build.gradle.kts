plugins {
    kotlin("jvm")
}

dependencies {

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
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