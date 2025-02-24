plugins {
    kotlin("jvm")
    `maven-publish`
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
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Spacetivity/SpaceInventories")
//            credentials {
//                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
//            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}