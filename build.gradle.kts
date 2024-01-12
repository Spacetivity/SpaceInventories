import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.9.0"
}

allprojects {
    group = "net.spacetivity.inventory"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://nexus.spacetivity.net/repository/maven-public/")
            credentials {
                username = property("nexusUsername") as String
                password = property("nexusPassword") as String
            }
        }
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    dependencies {
		compileOnly("com.google.code.gson:gson:2.10.1")
		compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    }

	tasks.test {
		useJUnitPlatform()
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}
}