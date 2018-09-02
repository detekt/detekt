---
title: "Run detekt using Gradle Kotlin DSL"
keywords: detekt static analysis code kotlin
tags: [getting_started gradle]
sidebar: 
permalink: kotlindsl.html
folder: gettingstarted
summary:
---

All information from Gradle Groovy DSL are still valid, but the
DSL to apply the plugin changes slightly.

For gradle version >= 4.1

```kotlin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

buildscript {
    repositories {
        jcenter()
    }
}
plugins {
    id("io.gitlab.arturbosch.detekt").version("1.0.0.[version]")
}

configure<DetektExtension> {
    version = "1.0.0.[version]"
    profile("main", Action {
        input = "src/main/kotlin"
        config = "detekt.yml"
        filters = ".*/resources/.*,.*/tmp/.*"
        output = "reports"
        outputName = "detekt-report"
        baseline = "reports/baseline.xml"
    })
}
```
