plugins {
    kotlin("jvm") version "2.4.0-RC"
    id("com.gradleup.shadow") version "8.3.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "net.azisaba"
version = "1.0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(8)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
