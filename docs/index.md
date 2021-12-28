---
title: "detekt"
keywords: detekt static analysis code kotlin
sidebar: 
permalink: index.html
summary:
---

![detekt logo](images/logo.svg "detekt logo")
![detekt in action](images/detekt_in_action.png "detekt in action")

### Features

- Code smell analysis for your Kotlin projects
- Complexity report based on logical lines of code, McCabe complexity and amount of code smells
- Highly configurable (rule set or rule level)
- Suppress findings with Kotlin's `@Suppress` and Java's `@SuppressWarnings` annotations
- Specify code smell thresholds to break your build or print a warning
- Code Smell baseline and ignore lists for legacy projects
- [Gradle plugin](pages/gettingstarted/gradle.md) for code analysis via Gradle builds
- [SonarQube integration](https://github.com/detekt/sonar-kotlin)
- Extensible by own rule sets and `FileProcessListener's`
- [IntelliJ integration](https://github.com/detekt/detekt-intellij-plugin)
- Unofficial [Maven plugin](https://github.com/Ozsie/detekt-maven-plugin) by [Ozsie](https://github.com/Ozsie)

### Quick Start with Gradle

Apply the following configuration to your Gradle project build file:

```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt").version("{{ site.detekt_version }}")
}

repositories {
    mavenCentral()
}
```

The format is very similar if you use the Gradle Groovy DSL. You can find what is the **latest version of detekt** in
the [release notes](/detekt/changelog.html).

Once you have set up detekt in your project, simply run `gradle detekt`.

To change the default behaviour of detekt rules, first generate yourself a detekt configuration file by running the
`detektGenerateConfig` task and applying any changes to the generated file.

Don't forget to reference the newly generated config inside the `detekt { }` closure. Optionally, it is possible to
slim down the configuration file to only the changes from the default configuration, by applying the
`buildUponDefaultConfig` option:

```kotlin
detekt {
    toolVersion = "{{ site.detekt_version }}"
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}
```

To enable/disable detekt reports use the `withType` method to set defaults for all detekt tasks at once:
```kotlin
// Kotlin DSL
tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}
```

```groovy
// Groovy DSL
tasks.withType(Detekt).configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}
```

See [reporting](https://detekt.github.io/detekt/reporting.html) docs for more details on configuring reports.

### Adding more rule sets

detekt itself provides a wrapper over [ktlint](https://github.com/pinterest/ktlint) as a `formatting` rule set
which can be easily added to the gradle configuration:

```gradle
dependencies {
    detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:{{ site.detekt_version }}"
}
```

Likewise custom [extensions](https://detekt.github.io/detekt/extensions.html) can be added to detekt.

{% include links.html %}
