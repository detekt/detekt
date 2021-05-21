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

Detekt requires **Gradle 5.4** or higher. We, however, recommend using the version of Gradle that is [listed in this table](https://detekt.github.io/detekt/compatibility.html).

## <a name="tasks">Available plugin tasks</a>

The detekt Gradle plugin will generate multiple tasks:

- `detekt` - Runs a detekt analysis and complexity report on your source files. Configure the analysis inside the 
`detekt` closure. By default the standard rule set without any ignore list is executed on sources files located
 in `src/main/java` and `src/main/kotlin`. Reports are automatically generated in xml, html, txt, and sarif format and can be 
 found in `build/reports/detekt/detekt.[xml|html|txt|sarif]` respectively. Please note that the `detekt` task is automatically 
 run when executing `gradle check`.
- `detektGenerateConfig` - Generates a default detekt configuration file into your project directory.
- `detektBaseline` - Similar to `detekt`, but creates a code smell baseline. Further detekt runs will only feature new smells not in this list.

In addition to these standard tasks, the plugin will also generate a set of experimental tasks that have
[type resolution](type-resolution.md) enabled. This happens for both, pure JVM projects and Android projects that have
the Android Gradle Plugin applied:

- `detektMain` - Similar to `detekt`, but runs only on the `main` source set
  (Android: all production source sets)
- `detektTest` - Similar to `detekt`, but runs only on the `test` source set
  (Android: all JVM and Android Test source sets)
- `detektBaselineMain` - Similar to `detektBaseline`, but creates a baseline only for the `main` source set 
  (Android: multiple baselines for all production source sets)
- `detektBaselineTest` - Similar to `detektBaseline`, but creates a baseline only for the `test` source set
  (Android: multiple baselines for all JVM and Android Test source sets)
- Android-only: `detekt<Variant>` - Similar to `detekt`, but runs only on the specific (test) build variant
- Android-only: `detektBaseline<Variant>` - Similar to `detektBaseline`, but creates a baseline only for the
  specific (test) build variant
  
Baseline files that are generated for these specific source sets / build variants contain the name of the source set /
the name of the build variant in their name, unless otherwise configured, such as `detekt-main.xml` or 
`detekt-productionDebug.xml`.

If both, a `detekt-main.xml` and a `detekt.xml` baseline file exists in place, the more specific one - `detekt-main.xml` -
takes precendence when the `detektMain` task is executed, likewise for Android variant-specific baseline files.

_NOTE:_ When analyzing Android projects that make use of specific code generators, such as Data Binding, Kotlin synthetic
view accessors or else, you might see warnings output while Detekt runs. This is due to the inability to gather the
complete compile classpath from the Android Gradle Plugin ([upstream ticket](https://issuetracker.google.com/issues/158777988))
and can safely be ignored.

Use the Groovy or Kotlin DSL of Gradle to apply the detekt Gradle Plugin. You can further configure the Plugin
using the detekt closure as described [here](#closure).

### <a name="gradle">Configuration</a>

Using the plugins DSL:

#### Groovy DSL

```groovy
plugins {
    id "io.gitlab.arturbosch.detekt" version "{{ site.detekt_version }}"
}

repositories {
    mavenCentral()
}
```

#### Kotlin DSL

```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt").version("{{ site.detekt_version }}")
}

repositories {
    mavenCentral()
}
```

Using legacy plugin application (`buildscript{}`):

#### Groovy DSL

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
    mavenCentral()
}
```

#### Kotlin DSL

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
    mavenCentral()
}
```

### <a name="gradleandroid">Configuration for Android projects</a>

When using Android make sure to have detekt configured in the project level build.gradle file.

You can configure the plugin in the same way as indicated above.

#### Groovy DSL

```groovy
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:4.0.1"
    }
}

plugins {
    id "com.android.application"
    id "org.jetbrains.kotlin.android" version "1.4.0"
    id "io.gitlab.arturbosch.detekt" version "{{ site.detekt_version }}"
}

repositories {
    mavenCentral()
}
```

#### Kotlin DSL

```kotlin
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
    }
}

plugins {
    id("com.android.application")
    kotlin("android") version "1.4.0"
    id("io.gitlab.arturbosch.detekt") version "{{ site.detekt_version }}"
}

repositories {
    mavenCentral()
}
```

### <a name="closure">Options for detekt configuration closure</a>

#### Groovy DSL

```groovy
detekt {
    // Version of Detekt that will be used. When unspecified the latest detekt
    // version found will be used. Override to stay on the same version.
    toolVersion = "{{ site.detekt_version }}"
    
    // The directories where detekt looks for source files. 
    // Defaults to `files("src/main/java", "src/main/kotlin")`.
    input = files(
        "src/main/kotlin",
        "gensrc/main/kotlin"
    )
    
    // Builds the AST in parallel. Rules are always executed in parallel. 
    // Can lead to speedups in larger projects. `false` by default.
    parallel = false
    
    // Define the detekt configuration(s) you want to use. 
    // Defaults to the default detekt configuration.
    config = files("path/to/config.yml")
    
    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = false
    
    // Turns on all the rules. `false` by default.
    allRules = false
    
    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    baseline = file("path/to/baseline.xml")
    
    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    disableDefaultRuleSets = false
    
    // Adds debug output during task execution. `false` by default.
    debug = false                                         
    
    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    ignoreFailures = false
    
    // Android: Don't create tasks for the specified build types (e.g. "release")
    ignoredBuildTypes = ["release"]
    
    // Android: Don't create tasks for the specified build flavor (e.g. "production")
    ignoredFlavors = ["production"]
    
    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
    ignoredVariants = ["productionRelease"]
    
    // Specify the base path for file paths in the formatted reports. 
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir
    
    reports {
        // Enable/Disable XML report (default: true)
        xml {
            enabled = true                                
            destination = file("build/reports/detekt.xml")
        }
        // Enable/Disable HTML report (default: true)
        html {
            enabled = true
            destination = file("build/reports/detekt.html")
        }
        // Enable/Disable TXT report (default: true)
        txt {
            enabled = true                                
            destination = file("build/reports/detekt.txt")
        }
        // Enable/Disable SARIF report (default: false)
        sarif {
            enabled = true                                
            destination = file("build/reports/detekt.sarif")
        }
        custom {
            // The simple class name of your custom report.
            reportId = "CustomJsonReport"                   
            destination = file("build/reports/detekt.json")
        }
    }
}
```

#### Kotlin DSL

```kotlin
detekt {
    // Version of Detekt that will be used. When unspecified the latest detekt
    // version found will be used. Override to stay on the same version.
    toolVersion = "{{ site.detekt_version }}"
    
    // The directories where detekt looks for source files. 
    // Defaults to `files("src/main/java", "src/main/kotlin")`.
    input = files("src/main/java", "src/main/kotlin")     
    
    // Builds the AST in parallel. Rules are always executed in parallel. 
    // Can lead to speedups in larger projects. `false` by default.
    parallel = false
    
    // Define the detekt configuration(s) you want to use. 
    // Defaults to the default detekt configuration.
    config = files("path/to/config.yml")
    
    // Applies the config files on top of detekt's default config file. `false` by default.
    buildUponDefaultConfig = false
    
    // Turns on all the rules. `false` by default.
    allRules = false
    
    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
    baseline = file("path/to/baseline.xml")
    
    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    disableDefaultRuleSets = false
    
    // Adds debug output during task execution. `false` by default.
    debug = false                                         
    
    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    ignoreFailures = false
    
    // Android: Don't create tasks for the specified build types (e.g. "release")
    ignoredBuildTypes = listOf("release")
    
    // Android: Don't create tasks for the specified build flavor (e.g. "production")
    ignoredFlavors = listOf("production")
    
    // Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
    ignoredVariants = listOf("productionRelease")
    
    // Specify the base path for file paths in the formatted reports. 
    // If not set, all file paths reported will be absolute file path.
    basePath = projectDir
    
    reports {
        // Enable/Disable XML report (default: true)
        xml {
            enabled = true
            destination = file("build/reports/detekt.xml")
        }
        // Enable/Disable HTML report (default: true)
        html {
            enabled = true
            destination = file("build/reports/detekt.html")
        }
        // Enable/Disable TXT report (default: true)
        txt {
            enabled = true
            destination = file("build/reports/detekt.txt")
        }
        // Enable/Disable SARIF report (default: false)
        sarif {
            enabled = true                                
            destination = file("build/reports/detekt.sarif")
        }
        custom {
            // The simple class name of your custom report.
            reportId = "CustomJsonReport"
            destination = file("build/reports/detekt.json")
        }
    }
}
```

### Using Type Resolution

Type resolution is experimental and works only for [predefined tasks listed above](#a-nametasksavailable-plugin-tasksa)
or when implementing a custom detekt task with the `classpath` and `jvmTarget` properties present.

More information on type resolution are available on the [type resolution](type-resolution.md) page.

#### Groovy DSL

```groovy
tasks.detekt.jvmTarget = "1.8"
```

#### Kotlin DSL

```kotlin
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    this.jvmTarget = "1.8"
}
```

### <a name="excluding">Leveraging Gradle's SourceTask - Excluding and including source files</a>

A detekt task extends the Gradle `SourceTask` to be only scheduled when watched source files are changed.
It also allows to match files that should be excluded from the analysis.
To do this introduce a query on detekt tasks and define include and exclude patterns outside the detekt closure:

#### Groovy DSL

```groovy
detekt {
    ...
}

tasks.withType(io.gitlab.arturbosch.detekt.Detekt).configureEach {
    // include("**/special/package/**") // only analyze a sub package inside src/main/kotlin
    exclude("**/special/package/internal/**") // but exclude our legacy internal package
}
```

#### Kotlin DSL

```kotlin
detekt {
    ...
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    // include("**/special/package/**") // only analyze a sub package inside src/main/kotlin
    exclude("**/special/package/internal/**") // but exclude our legacy internal package
}
```

### <a name="customdetekttask">Defining custom detekt task</a>

Custom tasks for alternative configurations or different source sets can be defined by creating a custom task that
uses the type `Detekt`.

#### Groovy DSL

```groovy
tasks.register(name: myDetekt, type: io.gitlab.arturbosch.detekt.Detekt) {
    description = "Runs a custom detekt build."
    setSource(files("src/main/kotlin", "src/test/kotlin"))
    config.setFrom(files("$rootDir/config.yml"))
    debug = true
    reports {
        xml {
            destination = file("build/reports/mydetekt.xml")
        }
        html.destination = file("build/reports/mydetekt.html")
    }
    include '**/*.kt'
    include '**/*.kts'
    exclude 'resources/'
    exclude 'build/'
}
```

#### Kotlin DSL

```kotlin
tasks.register<io.gitlab.arturbosch.detekt.Detekt>("myDetekt") {
    description = "Runs a custom detekt build."
    setSource(files("src/main/kotlin", "src/test/kotlin"))
    config.setFrom(files("$rootDir/config.yml"))
    debug = true
    reports {
        xml {
            destination = file("build/reports/mydetekt.xml")
        }
        html.destination = file("build/reports/mydetekt.html")
    }
    include("**/*.kt")
    include("**/*.kts")
    exclude("resources/")
    exclude("build/")
}
```

### <a name="check-lifecycle">Disabling detekt from the check task</a>

Detekt tasks by default are verification tasks. They get executed whenever the Gradle check task gets executed.
This aligns with the behavior of other code analysis plugins for Gradle.

If you are adding detekt to an already long running project you may want to increase the code quality incrementally and therefore
exclude detekt from the check task.

#### Groovy DSL

```groovy
// TODO
```

#### Kotlin DSL

```kotlin
tasks.named("check").configure {
    this.setDependsOn(this.dependsOn.filterNot {
        it is TaskProvider<*> && it.name == "detekt"
    })
}
```

Instead of disabling detekt for the check task, you may want to increase the build failure threshold in the [configuration file](../configurations.md).

## <a name="idea">Integrating detekt inside your IntelliJ IDEA</a>

detekt comes with an [IntelliJ Plugin](https://plugins.jetbrains.com/plugin/10761-detekt) that you can install directly from the IDE. The plugin offers warning highlight directly inside the IDE as well as support for code formatting.

The source code of the plugin is available here: [detekt/detekt-intellij-plugin](https://github.com/detekt/detekt-intellij-plugin)
