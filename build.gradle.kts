plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.9.0"
  id("org.jetbrains.intellij") version "1.15.0"
}

group = "com.longinx"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies{
  // https://mvnrepository.com/artifact/com.jcraft/jsch
    implementation("com.jcraft:jsch:0.1.55")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set("2022.2.5")
  type.set("IC") // Target IDE Platform

  plugins.set(listOf("java"))
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
  }

  patchPluginXml {
    sinceBuild.set("222")
    untilBuild.set("233.*")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
