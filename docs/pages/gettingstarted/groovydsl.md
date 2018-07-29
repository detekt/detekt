---
title: "Run detekt using the Detekt Gradle Plugin"
keywords: detekt static analysis code kotlin
tags: [getting_started, gradle, plugin]
sidebar: 
permalink: groovydsl.html
folder: gettingstarted
summary:
---

#### <a name="tasks">Available plugin tasks</a>

The detekt Gradle plugin will generate multiple tasks

- `detekt` - Runs a detekt analysis and complexity report on your source files. Configure the analysis inside the 
`detekt` closure. By default the standard rule set without any white- or blacklist is executed on sources files located
 in `src/main/java` and `src/main/kotlin`. Reports are automatically generated in xml and html format and can be 
 found in `build/reports/detekt/detekt.[xml|html]` respectively. Please note that the `detekt` task is automatically 
 run when executing `gradle check`.
- `detektGenerateConfig` - Generates a default detekt configuration file into your project directory.
- `detektBaseline` - Similar to `detekt`, but creates a code smell baseline. Further detekt runs will only feature new smells not in this list.
- `detektIdeaFormat` - Uses a local `idea` installation to format your Kotlin (and other) code according to the specified `code-style.xml`.
- `detektIdeaInspect` Uses a local `idea` installation to run inspections on your Kotlin (and other) code according to the specified `inspections.xml` profile.

Use the Groovy or Kotlin DSL of Gradle to apply the detekt Gradle Plugin. You can further configure the Plugin
using the detekt closure as described [here](#closure).

##### <a name="gradlegroovy">Configuration when using groovy dsl</a>
For gradle version >= 2.1

```groovy
buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    id "io.gitlab.arturbosch.detekt" version "1.0.0.[version]"
}
```

For all gradle versions:

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
```

##### <a name="gradlekotlin">Configuration when using Kotlin DSL</a>
For gradle version >= 4.1

```kotlin
import io.gitlab.arturbosch.detekt.DetektExtension

buildscript {
    repositories {
        jcenter()
    }
}
plugins {
    id("io.gitlab.arturbosch.detekt").version("1.0.0.[version]")
}
```

##### <a name="gradleandroid">Configuration for Android projects</a>

When using Android make sure to have detekt configured in the project level build.gradle file.

You can configure the plugin in the same way as indicated above.
```groovy
buildscript {
    repositories {
//        maven { url "https://plugins.gradle.org/m2/" }
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.3'
//        classpath "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0.[version]"
    }

}
plugins {
    id "io.gitlab.arturbosch.detekt" version "1.0.0.[version]"
}

//apply plugin: 'io.gitlab.arturbosch.detekt'
```


##### <a name="closure">Options for detekt configuration closure</a>

```groovy
detekt {
    toolVersion = "1.0.0.[version]"                       // When unspecified the latest detekt version found, will be used. Override to stay on the same version.
    input = files(                                        // The directories where detekt looks for input files. Defaults to `files("src/main/java", "src/main/kotlin")`
        "src/main/kotlin",
        "gensrc/main/kotlin"
    )
    parallel = false                                      // Runs detekt in parallel. Can lead to speedups in larger projects. `false` by default.
    config = file("path/to/config.yml")                   // Define the detekt configuration you want to use. Defaults to the default detekt configuration.
    baseline = file("path/to/baseline.xml")               // Specifying a baseline file will ignore all findings that are saved in the baseline file.
    filters = ''                                          // Regular expression of paths that should be excluded separated by `;`. Defaults to `.*/test/.*;.*Test.kt';.*Spec.kt`
    disableDefaultRuleSets = false                        // Disables all default detekt rulesets and will only run detekt with custom rules defined in `plugins`. `false` by default.
    plugins = "other/optional/ruleset.jar"                // Additional jar file containing custom detekt rules.
    debug = false                                         // Adds debug output during task execution. `false` by default.
    reportsDir = file('build/detekt-reports')             // Output directory where the reports are created. Defaults to `build/reports/detekt`
    reports {
        xml.enabled = true                                // Enable/Disable XML report (default: true)
        xml.destination file("build/reports/detekt.xml")  // Path where XML report will be stored (default: `build/reports/detekt/detekt.xml`)
        html {                                            // Alternatively as nested closure
            enabled = true                                // Enable/Disable HTML report (default: true)
            destination file("build/reports/detekt.html") // Path where HTML report will be stored (default: `build/reports/detekt/detekt.html`)
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
    config = file("config.yml")
    debug = true
    reports {
        xml {
            destination = file("build/reports/failfast.xml")
        }
        html.destination = file("build/reports/failfast.html")
    }
}
```

###### Groovy DSL
```groovy
task detektFailFast(type: io.gitlab.arturbosch.detekt.Detekt) {
   description = "Runs a failfast detekt build."

   input = files("src/main/java")
   config = file("$rootDir/config.yml")
   debug = true
   reports {
       xml {
           destination = file("build/reports/failfast.xml")
       }
       html.destination = file("build/reports/failfast.html")
   }
}
```

##### <a name="idea">Configure a local idea for detekt</a>

- download the community edition of [Intellij IDEA](https://www.jetbrains.com/idea/download/)
- extract the file to your preferred location eg. `~/.idea`
- let detekt know about idea inside the `detekt-closure`
- extract `code-style.xml` and `inpect.xml` from idea settings (`Settings>CodeStyle>Scheme` and `Settings>Inspections>Profile`)
- run `detektIdeaFormat` or `detektIdeaInspect`
- all parameters in the following detekt-closure are mandatory for both tasks
- make sure that current or default profile have an input path specified!

```groovy
String userHome = System.getProperty("user.home")

detekt {
    idea {
        path = "$userHome/.idea"
        codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
        inspectionsProfile = "$userHome/.idea/inspect.xml"
        report = "project.projectDir/reports"
        mask = "*.kt,"
    }
}
```

For more information on using idea as a headless formatting/inspection tool see [here](https://www.jetbrains.com/help/idea/working-with-intellij-idea-features-from-command-line.html).
