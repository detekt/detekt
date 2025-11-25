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

// https://detekt.dev/docs/introduction/extensions
detekt {
  // // It's important to not lose all in-built default rules of detekt after applying custom extension
  buildUponDefaultConfig = true

  // this path is default and can be omitted here but let's have it explicitly here for clarity
  config.setFrom(file("config/detekt/detekt.yml"))
}
