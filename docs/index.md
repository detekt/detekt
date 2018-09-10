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

- Code smell analysis for your Kotlin projects
- Complexity report based on logical lines of code, McCabe complexity and amount of code smells
- Highly configurable (rule set or rule level)
- Suppress findings with Kotlin's `@Suppress` and Java's `@SuppressWarnings` annotations
- Specify code smell thresholds to break your build or print a warning
- Code Smell baseline and ignore lists for legacy projects
- [Gradle plugin](#gradleplugin) for code analysis via Gradle builds
- Gradle tasks to use local `IntelliJ` distribution for [formatting and inspecting](#idea) Kotlin code
- Optionally configure detekt for each sub module by using [profiles](#closure) (gradle-plugin)
- [SonarQube integration](https://github.com/arturbosch/sonar-kotlin)
- Extensible by own rule sets and `FileProcessListener's`
- [IntelliJ integration](https://github.com/arturbosch/detekt-intellij-plugin)
- Unofficial [Maven plugin](https://github.com/Ozsie/detekt-maven-plugin) by [Ozsie](https://github.com/Ozsie)

### Quick Start with Gradle

Apply following configuration to your gradle build file and run `gradle detekt`:

```kotlin
buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("[version]")
}

detekt {
    version = "[version]"
    input = files("src/main/kotlin")
    filters = ".*/resources/.*,.*/build/.*"
    config = files("path/to/config.yml")
}
```

If you want to change the default behaviour of detekt rules, first generate yourself a detekt configuration file and apply your changes:

`gradle detektGenerateConfig`

Then reference the config inside the defaultProfile-closure:

`config = files("default-detekt-config.yml")`

To enable/disable detekt reports and to configure their output directories edit the `detekt { }` closure:
```kotlin
detekt {
    xml {
        enabled = true
        destination = file("path/to/destination.xml")
    }
    html {
        enabled = true
        destination = file("path/to/destination.html")
    }
}
``` 

### Adding more rule sets

detekt itself provides a wrapper over [KtLint](https://github.com/shyiko/ktlint) as a `formatting` rule set
which can be easily added to the gradle configuration:

```gradle
dependencies {
    detekt "io.gitlab.arturbosch.detekt:detekt-formatting:[version]"
}
```

Likewise custom [extensions](https://arturbosch.github.io/detekt/extensions.html) can be added to detekt.

{% include links.html %}
