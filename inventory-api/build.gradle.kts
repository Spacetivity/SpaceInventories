plugins {
    kotlin("jvm")
}

dependencies {

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
                "http://37.114.42.133:8081/repository/maven-snapshots/"
            } else {
                "http://37.114.42.133:8081/repository/maven-releases/"
            }

            isAllowInsecureProtocol = true
            url = uri(repositoryUrl)

            credentials {
                username = property("nexusUsername") as String
                password = property("nexusPassword") as String
            }
        }
    }
}