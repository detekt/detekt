---
title: "Run detekt using Gradle Groovy DSL"
keywords: detekt static analysis code kotlin
tags: [getting_started, gradle]
sidebar: 
permalink: groovydsl.html
summary:
---

For new gradle versions >= 2.1:
 
```gradle
buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id "io.gitlab.arturbosch.detekt" version "1.0.0.[version]"
}

detekt {
    version = "1.0.0.[version]"
    defaultProfile {
        input = file("src/main/kotlin")
        config = file("detekt.yml")
        filters = ".*/resources/.*,.*/tmp/.*"
        output = file("reports")
        outputName = "detekt-report"
        baseline = file("reports/baseline.xml")
    }
}
```

For older gradle versions:

```groovy
buildscript {
  repositories {
    jcenter()
    maven { url "https://plugins.gradle.org/m2/" }
  }
  dependencies {
    classpath "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0.[version]"
  }
}

apply plugin: "io.gitlab.arturbosch.detekt"

detekt {
    version = "1.0.0.[version]"
    defaultProfile {
        input = file("src/main/kotlin")
        config = file("detekt.yml")
        filters = ".*/resources/.*,.*/tmp/.*"
        output = file("reports")
        outputName = "detekt-report"
        baseline = file("reports/baseline.xml")
    }
}
```
