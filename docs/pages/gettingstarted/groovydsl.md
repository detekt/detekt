---
title: "Run detekt using Gradle Groovy DSL"
keywords: detekt static analysis code kotlin
tags: [getting_started, gradle]
sidebar: 
permalink: groovydsl.html
folder: gettingstarted
summary:
---

- `detektCheck` - Runs a _detekt_ analysis. Configure the analysis inside the `detekt-closure`. By default the standard rule set is used without output report or  black- and whitelist checks.
- `detektGenerateConfig` - Generates a default detekt config file within your projects location.
- `detektBaseline` - Like `detektCheck`, but creates a code smell baseline. Further detekt runs will only feature new issues. 
- `detektIdeaFormat` - Uses a local `idea` installation to format your kotlin (and other) code according to the specified `code-style.xml`.
- `detektIdeaInspect` Uses a local `idea` installation to run inspections on your kotlin (and other) code according to the specified `inspections.xml` profile.

For new gradle versions >= 2.1:
 
```gradle
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
        config = file("detekt.yml")
        filters = ".*/resources/.*,.*/tmp/.*"
        output = file("reports")
        outputName = "detekt-report"
        baseline = file("reports/baseline.xml")
    }
}
```

For older gradle versions:

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

detekt {
    version = "1.0.0.[version]"
    defaultProfile {
        input = file("src/main/kotlin")
        config = file("detekt.yml")
        filters = ".*/resources/.*,.*/tmp/.*"
        output = file("reports")
        outputName = "detekt-report"
        baseline = file("reports/baseline.xml")
    }
}
```

### Options for detekt configuration closure

```groovy
detekt {
    version = "1.0.0.[version]"  // When unspecified the latest detekt version found will be used. Override to stay on the same version.
    profile = "override" // When not specified, the default profile will always be used. If specified, this profile will be used and merged with the default one.
    // A profile basically abstracts over the argument vector passed to detekt. 
    // Different profiles can be specified and used for different sub modules or testing code.
    // There will always be a default profile which is used as the baseline for detection.
    defaultProfile {
        // Which part of your project should be analyzed?
        input = file("src/main/kotlin")
        // Use $project.projectDir or to navigate inside your project 
        config = file("detekt.yml")
        // Use this parameter instead of config if your detekt yaml file is inside your resources. Is needed for multi project maven tasks.
        configResource = "/detekt.yml"
         // What paths to exclude? Use comma or semicolon to separate.
        filters = ".*/build/.*, .*/resources/.*"
        // Use this property to link to detekt extensions (comma or semicolon separated).
        ruleSets = "other/optional/ruleset.jar"
        // Disables the default rule set. Turn on this option if you want to just use detekt as the detection engine with your custom rule sets.
        disableDefaultRuleSets = false
         // Directory where output reports are stored (if present).
        output = file("reports")
        // This parameter is used to derive the output report name
        outputName = "my-module"
        // If present all current findings are saved in a baseline.xml to only consider new code smells for further runs.
        baseline = file("reports/baseline.xml")
        // Use this flag if your project has more than ~200 files. 
        // This will create a parallel stream to parse all kotlin files. 
        // The analysis is always in parallel.
        parallel = true 
   }
   
    // Feel free to declare as many profile as you need e.g. for each sub module.
    // Be aware that the default profile is always used as a backup. 
    // If you profile does not use for example `input` it will be looked up in the default profile.
    profiles {
       // Definines a secondary profile. This profile can always be used when specified in the `profile` property.
       // A system property can also be used like this: `gradle detektCheck -Ddetekt.profile=override` 
       override {
           // Declares a new configuration file. 
           // If a rule or property cannot be looked up here, the default profile config is used.
           config = file("$projectDir/other-config-file.yml")
       }
    }
}
```

### Configure a local IntelliJ instance for detekt

- download the community edition of [Intellij IDEA](https://www.jetbrains.com/idea/download/)
- extract the file to your preferred location eg. `~/.idea`
- let detekt know about idea inside the `detekt-closure`
- extract `code-style.xml` and `inpect.xml` from idea settings (`Settings>CodeStyle>Scheme` and `Settings>Inspections>Profile`)
- run `detektIdeaFormat` or `detektIdeaInspect`
- all parameters in the following detekt-closure are mandatory for both tasks
- make sure that current or default profile have an input path specified!

```groovy
String USER_HOME = System.getProperty("user.home")

detekt {  
    defaultProfile {
        ...
    }
    idea {
        path = "$USER_HOME/.idea"
        codeStyleScheme = "$USER_HOME/.idea/idea-code-style.xml"
        inspectionsProfile = "$USER_HOME/.idea/inspect.xml"
        report = "project.projectDir/reports"
        mask = "*.kt,"
    }
}
```

For more information on using idea as a headless formatting/inspection tool see [here](https://www.jetbrains.com/help/idea/working-with-intellij-idea-features-from-command-line.html).

