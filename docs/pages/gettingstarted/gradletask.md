---
title: "Run detekt using Gradle Task"
keywords: gradle task
sidebar: 
permalink: gradletask.html
folder: gettingstarted
summary:
---

1. Add following lines to your build.gradle file.
2. Run `gradle detekt`
3. Add `check.dependsOn detekt` if you want to run _detekt_ on every `build`

###### Groovy DSL
```groovy
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

configurations {
	detekt
}

task detekt(type: JavaExec) {
	main = "io.gitlab.arturbosch.detekt.cli.Main"
	classpath = configurations.detekt
	def input = "$projectDir"
	def config = "$projectDir/detekt.yml"
	def exclude = ".*/build/.*,.*/resources/.*"
	def params = [ '-i', input, '-c', config, '-ex', exclude]
	args(params)
}

dependencies {
	detekt 'io.gitlab.arturbosch.detekt:detekt-cli:{{ site.detekt_version }}'
}
```

###### Kotlin DSL
```kotlin
// TODO
```
