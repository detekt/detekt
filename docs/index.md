---
title: "detekt"
keywords: detekt static analysis code kotlin
tags: [features, quick_start]
sidebar: 
permalink: index.html
summary:
---

![detekt in action](images/detekt_in_action.png "detekt in action")

### Features

- code smell analysis for your kotlin projects
- complexity report based on logical lines of code, McCabe complexity and amount of code smells
- highly configurable (rule set or rule level)
- suppress findings with Kotlin's @Suppress and Java's @SuppressWarnings annotations
- specify code smell thresholds to break your build or print a warning
- code Smell baseline and ignore lists for legacy projects
- [gradle plugin](#gradleplugin) for code analysis via Gradle builds
- gradle tasks to use local `intellij` distribution for [formatting and inspecting](#idea) kotlin code
- optionally configure detekt for each sub module by using [profiles](#closure) (gradle-plugin)
- [sonarqube integration](https://github.com/arturbosch/sonar-kotlin)
- extensible by own rule sets and `FileProcessListener's`
- [intellij integration](https://github.com/arturbosch/detekt-intellij-plugin)
- unofficial [maven plugin](https://github.com/Ozsie/detekt-maven-plugin) by the user [Ozsie](https://github.com/Ozsie)

### Quick Start with Gradle

Apply following configuration to your gradle build file and run `gradle detektCheck`:

```groovy
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
        filters = ".*/resources/.*,.*/build/.*"
    }
}
```

If you want to change the default behaviour of detekt rules, first generate yourself a detekt configuration file and apply your changes:

`gradle detektGenerateConfig`

Then reference the config inside the defaultProfile-closure:

`config = file("default-detekt-config.yml")`

If you need a textual report, specify the output directory and the reports name in the `defaultProfile`-closure:

```
output = file("reports")
outputName = "detekt"
``` 

### Adding more rule sets

detekt itself provides a wrapper over [KtLint](https://github.com/shyiko/ktlint) as a `formatting` rule set
which can be easily added to the gradle configuration:

```gradle
dependencies {
    detekt "io.gitlab.arturbosch.detekt:detekt-formatting:1.0.0.[version]"
}
```

Likewise custom [extensions](https://arturbosch.github.io/detekt/extensions.html) can be added to detekt.

{% include links.html %}
