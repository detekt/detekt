---
title: "Run detekt using Gradle Kotlin DSL"
keywords: detekt static analysis code kotlin
sidebar: 
permalink: kotlindsl.html
folder: gettingstarted
summary:
---

All information from Gradle Groovy DSL are still valid, but the
DSL to apply the plugin changes slightly.

##### <a name="gradlekotlin">Configuration when using Kotlin DSL</a> 

```kotlin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

repositories {
    jcenter()

    // or

    mavenCentral()
    jcenter {
        content {
            // just allow to include kotlinx projects
            // detekt needs 'kotlinx-html' for the html report
            includeGroup "org.jetbrains.kotlinx"
        }
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("{{ site.detekt_version }}")
}

detekt {
    toolVersion = "{{ site.detekt_version }}"                                 // Version of the Detekt CLI that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
    input = files("src/main/java", "src/main/kotlin")     // The directories where detekt looks for source files. Defaults to `files("src/main/java", "src/main/kotlin")`.
    parallel = false                                      // Builds the AST in parallel. Rules are always executed in parallel. Can lead to speedups in larger projects. `false` by default.
    config = files("path/to/config.yml")                  // Define the detekt configuration(s) you want to use. Defaults to the default detekt configuration.
    buildUponDefaultConfig = false                        // Interpret config files as updates to the default config. `false` by default.
    baseline = file("path/to/baseline.xml")               // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    disableDefaultRuleSets = false                        // Disables all default detekt rulesets and will only run detekt with custom rules defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    debug = false                                         // Adds debug output during task execution. `false` by default.
    ignoreFailures = false                                // If set to `true` the build does not fail when the maxIssues count was reached. Defaults to `false`.
    reports {
        xml {
            enabled = true                                // Enable/Disable XML report (default: true)
            destination = file("build/reports/detekt.xml")  // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
        }
        html {
            enabled = true                                // Enable/Disable HTML report (default: true)
            destination = file("build/reports/detekt.html") // Path where HTML report will be stored (default: `build/reports/detekt/detekt.html`)
        }
        txt {
            enabled = true                                // Enable/Disable TXT report (default: true)
            destination = file("build/reports/detekt.txt") // Path where TXT report will be stored (default: `build/reports/detekt/detekt.txt`)
        }
        custom {
            reportId = "CustomJsonReport"                   // The simple class name of your custom report.
            destination = file("build/reports/detekt.json") // Path where report will be stored
        }
    }
}
```

##### Using Type Resolution

Type resolution is experimental and works only for predefined `detektMain` and `detektTest` tasks or when implementing a 
custom detekt task with the `classpath` and `jvmTarget` properties present.

```kotlin
tasks {
    withType<Detekt> {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        this.jvmTarget = "1.8"
    }
}
```

##### <a name="excluding">Leveraging Gradle's SourceTask - Excluding and including source files</a>

A detekt task extends the Gradle `SourceTask` to be only scheduled when watched source files are changed.
It also allows to match files that should be excluded from the analysis.
To do this introduce a query on detekt tasks and define include and exclude patterns outside the detekt closure:

```kotlin
detekt {
    ...
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
    // include("**/special/package/**") // only analyze a sub package inside src/main/kotlin
    exclude("**/special/package/internal/**") // but exclude our legacy internal package
}
```

##### <a name="customdetekttask">Defining custom detekt task</a>

Custom tasks for alternative configurations or different source sets can be defined by creating a custom task that
uses the type `Detekt`.

###### Kotlin DSL
```kotlin
task<io.gitlab.arturbosch.detekt.Detekt>("detektFailFast") {
    description = "Runs a failfast detekt build."
    source = files("src/main/kotlin", "src/test/kotlin")
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
