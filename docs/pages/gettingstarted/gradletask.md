---
title: "Run detekt using Gradle Task"
keywords: gradle task
tags: [getting_started, gradle]
sidebar: 
permalink: gradletask.html
folder: gettingstarted
summary:
---

1. Add following lines to your build.gradle file.
2. Run `gradle detekt`
3. Add `check.dependsOn detekt` if you want to run _detekt_ on every `build`

```groovy
repositories {
    jcenter()
}

configurations {
	detekt
}

task detekt(type: JavaExec) {
	main = "io.gitlab.arturbosch.detekt.cli.Main"
	classpath = configurations.detekt
	def input = "$projectDir"
	def config = "$projectDir/detekt.yml"
	def filters = ".*/build/.*,.*/resources/.*"
	def rulesets = ""
	def params = [ '-i', input, '-c', config, '-f', filters, '-r', rulesets]
	args(params)
}

dependencies {
	detekt 'io.gitlab.arturbosch.detekt:detekt-cli:1.0.0.[version]'
}
```
