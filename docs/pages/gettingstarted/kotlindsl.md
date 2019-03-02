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


##### <a name="gradlekotlin">Configuration when using Kotlin DSL</a>

For gradle version >= 4.1

```kotlin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    jcenter()
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("[version]")
}

detekt {
    toolVersion = "[version]"                             // Version of the Detekt CLI that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
    input = files(                                        // The directories where detekt looks for input files. Defaults to `files("src/main/java", "src/main/kotlin")`.
        "src/main/kotlin",
        "gensrc/main/kotlin"
    )
    parallel = false                                      // Builds the AST in parallel. Rules are always executed in parallel. Can lead to speedups in larger projects. `false` by default.
    config = files("path/to/config.yml")                  // Define the detekt configuration(s) you want to use. Defaults to the default detekt configuration.
    baseline = file("path/to/baseline.xml")               // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    disableDefaultRuleSets = false                        // Disables all default detekt rulesets and will only run detekt with custom rules defined in `plugins`. `false` by default.
    plugins = "other/optional/ruleset.jar"                // Additional jar file containing custom detekt rules.
    debug = false                                         // Adds debug output during task execution. `false` by default.
    reports {
        xml {
            enabled = true                                // Enable/Disable XML report (default: true)
            destination = file("build/reports/detekt.xml")  // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
        }
        html {
            enabled = true                                // Enable/Disable HTML report (default: true)
            destination = file("build/reports/detekt.html") // Path where HTML report will be stored (default: `build/reports/detekt/detekt.html`)
        }
    }
}
```

##### <a name="customdetekttask">Defining custom detekt task</a>

Custom tasks for alternative configurations or different source sets can be defined by creating a custom task that
uses the type `Detekt`.

###### Kotlin DSL
```kotlin
task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
    description = "Runs a failfast detekt build."

    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("$rootDir/config.yml")
    debug = true
    reports {
        xml {
            destination = file("build/reports/failfast.xml")
        }
        html.destination = file("build/reports/failfast.html")
    }
    include("**/*.kt")
    include("**/*.kts")
    exclude("resources/")
    exclude("build/")
}
```


##### <a name="idea">Configure a local IDEA for detekt</a>

- Download the community edition of [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
- Extract the file to your preferred location eg. `~/.idea`
- Let detekt know about idea inside the `detekt-closure`
- Extract `code-style.xml` and `inpect.xml` from idea settings (`Settings>CodeStyle>Scheme` and `Settings>Inspections>Profile`)
- Run `detektIdeaFormat` or `detektIdeaInspect`
- All parameters in the following detekt-closure are mandatory for both tasks

```kotlin
val userHome = System.getProperty("user.home")

detekt {
    idea {
        path = "$userHome/.idea"
        codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
        inspectionsProfile = "$userHome/.idea/inspect.xml"
        report = "$project.projectDir/reports"
        mask = "*.kt,"
    }
}
```

For more information on using idea as a headless formatting/inspection tool see [here](https://www.jetbrains.com/help/idea/working-with-intellij-idea-features-from-command-line.html).
