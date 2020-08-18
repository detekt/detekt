---
title: "Run detekt using the Detekt Gradle Plugin"
keywords: detekt static analysis code kotlin
sidebar: 
permalink: gradle.html
folder: gettingstarted
redirect_from:
 - /groovydsl.html
 - /kotlindsl.html
summary:
---

detekt requires Gradle 5.3 or higher.

#### <a name="tasks">Available plugin tasks</a>

The detekt Gradle plugin will generate multiple tasks

- `detekt` - Runs a detekt analysis and complexity report on your source files. Configure the analysis inside the 
`detekt` closure. By default the standard rule set without any ignore list is executed on sources files located
 in `src/main/java` and `src/main/kotlin`. Reports are automatically generated in xml, html and txt format and can be 
 found in `build/reports/detekt/detekt.[xml|html|txt]` respectively. Please note that the `detekt` task is automatically 
 run when executing `gradle check`.
- `detektGenerateConfig` - Generates a default detekt configuration file into your project directory.
- `detektBaseline` - Similar to `detekt`, but creates a code smell baseline. Further detekt runs will only feature new smells not in this list.
- `detektIdeaFormat` - Uses a local `idea` installation to format your Kotlin (and other) code according to the specified `code-style.xml`.
- `detektIdeaInspect` - Uses a local `idea` installation to run inspections on your Kotlin (and other) code according to the specified `inspections.xml` profile.

Use the Groovy or Kotlin DSL of Gradle to apply the detekt Gradle Plugin. You can further configure the Plugin
using the detekt closure as described [here](#closure).

##### <a name="gradle">Configuration</a>

Using the plugins DSL:

###### Groovy DSL
```groovy
plugins {
    id "io.gitlab.arturbosch.detekt" version "{{ site.detekt_version }}"
}

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
```

###### Kotlin DSL
```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt").version("{{ site.detekt_version }}")
}

repositories {
    jcenter()

    // or

    mavenCentral()
    jcenter {
        content {
            // just allow to include kotlinx projects
            // detekt needs 'kotlinx-html' for the html report
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}
```

Using legacy plugin application (`buildscript{}`):

###### Groovy DSL
```groovy
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:{{ site.detekt_version }}"
    }
}

apply plugin: "io.gitlab.arturbosch.detekt"

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
```

###### Kotlin DSL
```kotlin
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:{{ site.detekt_version }}")
    }
}

apply(plugin = "io.gitlab.arturbosch.detekt")

repositories {
    jcenter()

    // or

    mavenCentral()
    jcenter {
        content {
            // just allow to include kotlinx projects
            // detekt needs 'kotlinx-html' for the html report
            includeGroup("org.jetbrains.kotlinx")
        }
    }
}
```

##### <a name="gradleandroid">Configuration for Android projects</a>

When using Android make sure to have detekt configured in the project level build.gradle file.

You can configure the plugin in the same way as indicated above.

###### Groovy DSL
```groovy
buildscript {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.0.0"
        classpath "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:{{ site.detekt_version }}"
    }
}

apply plugin: "io.gitlab.arturbosch.detekt"

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
```

###### Kotlin DSL
```kotlin
// TODO
```

##### <a name="closure">Options for detekt configuration closure</a>

###### Groovy DSL
```groovy
detekt {
    toolVersion = "{{ site.detekt_version }}"                                 // Version of Detekt that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
    input = files(                                        // The directories where detekt looks for source files. Defaults to `files("src/main/java", "src/main/kotlin")`.
        "src/main/kotlin",
        "gensrc/main/kotlin"
    )
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
            destination = file("build/reports/detekt.xml") // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
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

###### Kotlin DSL
```kotlin
detekt {
    toolVersion = "{{ site.detekt_version }}"                                 // Version of Detekt that will be used. When unspecified the latest detekt version found will be used. Override to stay on the same version.
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
            destination = file("build/reports/detekt.xml") // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
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
            reportId = "CustomJsonReport"                 // The simple class name of your custom report.
            destination = file("build/reports/detekt.json") // Path where report will be stored
        }
    }
}
```

##### Using Type Resolution

Type resolution is experimental and works only for predefined `detektMain` and `detektTest` tasks or when implementing a 
custom detekt task with the `classpath` and `jvmTarget` properties present.

###### Groovy DSL
```groovy
tasks.detekt.jvmTarget = "1.8"
```

###### Kotlin DSL
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

###### Groovy DSL
```groovy
detekt {
    ...
}

tasks.withType(io.gitlab.arturbosch.detekt.Detekt) {
    // include("**/special/package/**") // only analyze a sub package inside src/main/kotlin
    exclude("**/special/package/internal/**") // but exclude our legacy internal package
}
```

###### Kotlin DSL
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

###### Groovy DSL
```groovy
task detektFailFast(type: io.gitlab.arturbosch.detekt.Detekt) {
    description = "Runs a failfast detekt build."
    source = files("src/main/java")
    config.from(files("$rootDir/config.yml"))
    debug = true
    reports {
        xml {
            destination = file("build/reports/failfast.xml")
        }
        html.destination = file("build/reports/failfast.html")
    }
    include '**/*.kt'
    include '**/*.kts'
    exclude 'resources/'
    exclude 'build/'
}
```

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

##### <a name="check-lifecycle">Disabling detekt from the check task</a>

Detekt tasks by default are verification tasks. They get executed whenever the Gradle check task gets executed.
This aligns with the behavior of other code analysis plugins for Gradle.

If you are adding detekt to an already long running project you may want to increase the code quality incrementally and therefore
exclude detekt from the check task.

###### Groovy DSL
```groovy
// TODO
```

###### Kotlin DSL
```kotlin
tasks.getByName("check") {
    this.setDependsOn(this.dependsOn.filterNot {
        it is TaskProvider<*> && it.name == "detekt"
    })
}
```

Instead of disabling detekt for the check task, you may want to increase the build failure threshold in the [configuration file](../configurations.md).

##### <a name="idea">Integrating detekt inside your IntelliJ IDEA</a>

detekt comes with an [IntelliJ Plugin](https://plugins.jetbrains.com/plugin/10761-detekt) that you can install directly from the IDE. The plugin offers warning highlight directly inside the IDE as well as support for code formatting.

The source code of the plugin is available here: [detekt/detekt-intellij-plugin](https://github.com/detekt/detekt-intellij-plugin)
