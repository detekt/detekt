---
title: "detekt"
keywords: detekt static analysis code kotlin
tags: [features, quick_start]
sidebar: 
permalink: index.html
summary:
---

[//]: {% include note.html content="If you're cloning this theme, you're probably writing documentation of some kind. I have a blog on technical writing here called <a alt='technical writing blog' href='http://idratherbewriting.com'>I'd Rather Be Writing</a>. If you'd like to stay updated with the latest trends, best practices, and other methods for writing documentation, consider <a href='https://tinyletter.com/tomjoht'>subscribing</a>. I also have a site on <a href='http://idratherbewriting.com/learnapidoc'>writing API documentation</a>. Also, if you want a slightly different Jekyll documentation theme, see my <a href='https://github.com/amzn/jekyll-doc-project'>Jekyll doc project theme</a>." %}

![detekt in action](images/detekt_in_action.png "detekt in action")

### Features

- code smell analysis for your kotlin projects
- complexity report based on logical lines of code, McCabe complexity and amount of code smells
- highly configurable (rule set or rule level)
- suppress findings with Kotlin's @Suppress and Java's @SuppressWarnings annotations
- specify code smell thresholds to break your build or print a warning
- code Smell baseline and ignore lists for legacy projects
- [gradle plugin](#gradleplugin) for code analysis via Gradle builds
- gradle tasks to use local `intellij` distribution for [formatting and inspecting](#idea) kotlin code
- optionally configure detekt for each sub module by using [profiles](#closure) (gradle-plugin)
- [sonarqube integration](https://github.com/arturbosch/sonar-kotlin)
- extensible by own rule sets and `FileProcessListener's`
- [intellij integration](https://github.com/arturbosch/detekt-intellij-plugin)
- unofficial [maven plugin](https://github.com/Ozsie/detekt-maven-plugin) by the user [Ozsie](https://github.com/Ozsie)

### Quick Start with Gradle

Apply following configuration to your gradle build file and run `gradle detektCheck`:

```groovy
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
        filters = ".*/resources/.*,.*/build/.*"
    }
}
```

If you want to change the default behaviour of detekt rules, first generate yourself a detekt configuration file and apply your changes:

`gradle detektGenerateConfig`

Then reference the config inside the defaultProfile-closure:

`config = file("default-detekt-config.yml")`

If you need a textual report, specify the output directory and the reports name defaultProfile-closure:

```
output = file("reports")
outputName = "detekt"
``` 

{% include links.html %}
