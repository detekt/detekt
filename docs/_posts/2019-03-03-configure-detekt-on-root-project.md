---
title:  "Howto: configure detekt for gradle root project"
published: true
permalink: howto-buildupondefaultconfig.html
summary: "Configure a new task based on _detekt_ to analyze the whole project on one run."
tags: [guides]
---

When configuring _detekt_ for your _Gradle_ based project, you basically have two options:
- for each sub module a new gradle task should be created
- or one __uber__-task analyzes your whole project

For the first option, please see how [detekt](https://github.com/detekt/detekt) itself creates a task for each module:
```gradle
subprojecs {
...
    detekt {
        debug = true
        toolVersion = usedDetektVersion
        buildUponDefaultConfig = true
        config = files(project.rootDir.resolve("reports/failfast.yml"))
        baseline = project.rootDir.resolve("reports/baseline.xml")
    
        reports {
            xml.enabled = true
            html.enabled = true
        }
    
        idea {
            path = "$userHome/.idea"
            codeStyleScheme = "$userHome/.idea/idea-code-style.xml"
            inspectionsProfile = "$userHome/.idea/inspect.xml"
            report = "project.projectDir/reports"
            mask = "*.kt"
        }
    }
}
```

Sometimes it makes sense to add an additional _detekt_ task which runs over the whole project and produces one big report. 
Such a setup could look like the following in its simplest form:
```gradle
plugins {
    id "io.gitlab.arturbosch.detekt" version "1.0.0-RC14"
}

repositories {
    jcenter()
}

detekt {
    input = files(rootProject.rootDir)
    buildUponDefaultConfig = true
}
```
Make sure to specify the `input` parameter or no sources are found and _detekt_ won't run!

If you need more fine grained _detekt_ tasks, you could register more tasks using the _Detekt_ task as the base task.
Using the _Kotlin-Dsl_ it could look like this:
```gradle
val detektAll by tasks.registering(Detekt::class) {
    description = "Runs over whole code base without the starting overhead for each module."
    parallel = true
    buildUponDefaultConfig = true
    setSource(files(projectDir))
    config = files(project.rootDir.resolve("reports/failfast.yml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("resources/")
    exclude("build/")
    baseline.set(project.rootDir.resolve("reports/baseline.xml"))
    reports {
        xml.enabled = false
        html.enabled = false
    }
}
```

{% include links.html %}
