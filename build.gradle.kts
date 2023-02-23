import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.8.0"
}

allprojects {
    group = "net.spacetivity.template.multiplugin"
    version = "1.0-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://nexus.neptunsworld.com/repository/maven-group/")
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

    val exposedVersion: String by project

    dependencies {
		compileOnly("com.google.code.gson:gson:2.10.1")
		compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

		implementation(group = "org.mariadb.jdbc", name = "mariadb-java-client", version = "3.0.7")

		implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
		implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
		implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

		implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.25")
		implementation(group = "org.slf4j", name = "slf4j-simple", version = "1.7.25")
		implementation(kotlin("stdlib-jdk8"))
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}