plugins {
  kotlin("jvm") version "2.2.21"
  id("dev.detekt") version "2.0.0-alpha.1"
}

repositories {
  mavenCentral()
  maven {
    url = uri("test-maven-repo")
  }
}

dependencies {
  detektPlugins("io.gitlab.arturbosch:detekt-sample-extension:1.2.3")
}
