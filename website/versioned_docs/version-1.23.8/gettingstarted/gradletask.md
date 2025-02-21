---
title: "Run detekt using Gradle Task"
keywords: [gradle, task]
sidebar: 
permalink: gradletask.html
folder: gettingstarted
summary:
sidebar_position: 3
---

1. Add following lines to your build.gradle file.
2. Run `gradle detekt`

###### Groovy DSL
```groovy
repositories {
    mavenCentral()
}

configurations {
	detekt
}

def detektTask = tasks.register("detekt", JavaExec) {
	mainClass = "io.gitlab.arturbosch.detekt.cli.Main"
	classpath = configurations.detekt

	def input = "$projectDir"
	def config = "$projectDir/detekt.yml"
	def exclude = ".*/build/.*,.*/resources/.*"
	def params = [ '-i', input, '-c', config, '-ex', exclude]

	args(params)
}

dependencies {
	detekt 'io.gitlab.arturbosch.detekt:detekt-cli:[detekt_version]'
}

// Remove this line if you don't want to run detekt on every build
check.dependsOn detektTask
```

###### Kotlin DSL
```kotlin
repositories {
    mavenCentral()
}

val detekt by configurations.creating

val detektTask = tasks.register<JavaExec>("detekt") {
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    classpath = detekt

    val input = projectDir
    val config = "$projectDir/detekt.yml"
    val exclude = ".*/build/.*,.*/resources/.*"
    val params = listOf("-i", input, "-c", config, "-ex", exclude)

    args(params)
}

dependencies {
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:[detekt_version]")
}

// Remove this block if you don't want to run detekt on every build
tasks.check {
    dependsOn(detektTask)
}
```
