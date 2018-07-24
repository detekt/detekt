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

The detekt Gradle plugin will generate `detekt` tasks for each of your source sets. For a basic project this will result
in a `detektMain` task which will check all `main` sourcesets of the project. The `detektTest` task will run detekt on
all `test` sourcesets of the project

- `detekt[SourceSet]` - Runs a detekt analysis and complexity report on the given source set. Configure the analysis inside the `detekt` closure. By default the standard rule set is used without output report or black- and whitelist checks.
- `detektGenerateConfig` - Generates a default detekt configuration file into your project directory.
- `detektBaseline` - Similar to `detekt[SourceSet]`, but creates a code smell baseline. Further detekt runs will only feature new smells not in this list.
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
    toolVersion = "1.0.0.[version]"                                  // When unspecified the latest detekt version found, will be used. Override to stay on the same version.
    parallel = false                                                 // Runs detekt in parallel. Can lead to speedups in larger projects. `false` by default.
    config = project.resources.text.fromFile("path/to/config.yml")   // Define the detekt configuration you want to use.
    configFile = file("path/to/config.yml")                          // Define the detekt configuration you want to use.
    baseline = file("path/to/baseline.xml")                          // Specifying a baseline file will ignore all findings that are saved in the baseline file.
    filters = ''                                                     // Regular expression of paths that should be excluded.
    disableDefaultRuleSets = false                                   // Disables all default detekt rulesets and will only run detekt with custom rules defined in `plugins`.
    plugins = "other/optional/ruleset.jar"                           // Jar file containing custom detekt rules.
}
```

##### <a name="gradlepluginreports">Customizing Detekt reports</a>

You can configure the reports detekt outputs with the following configuration in your `build.gradle` file:

```groovy
tasks.withType(io.gitlab.arturbosch.detekt.Detekt) {
    reports {
        xml {
            enabled true                                             // Enable/Disable XML report (default: true)
            destination file("build/reports/detekt.xml")             // Path where XML report will be stored (default: build/reports/detekt/[sourceset].xml)
        }
        html {
            enabled true                                             // Enable/Disable HTML report (default: true)
            destination file("build/reports/detekt.html")            // Path where HTML report will be stored (default: build/reports/detekt/[sourceset].html)
        }
    }
}
```

##### <a name="customdetekttask">Defining custom detekt</a>

Custom tasks for alternative configurations or different source sets can be defined by creating a custom task that
uses the type `Detekt`.

```groovy
task customDetektTask(type: io.gitlab.arturbosch.detekt.Detekt) {
		description = "Runs a custom detekt task."

		source = sourceSets.getAt("main").allSource                              // Define the source set this task should run for
		configFile = file("${rootProject.projectDir}/reports/failfast.yml")      // Define the configuration file that should be used
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
