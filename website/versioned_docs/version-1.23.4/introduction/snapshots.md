---
id: snapshots
title: "Using Snapshots"
keywords: [snapshot, releases]
summary: This page explains how you can setup snapshots for your detekt build to test the latest unreleased features. 
sidebar_position: 10
---

This page explains how you can use our **latest snapshots** to test upcoming unreleased features.

## Where to download snapshots

You can find the latest snapshot on [sonatype](https://central.sonatype.com/repository/maven-snapshots/). A new snapshot is published after every merge to `main` from the [Deploy Snapshot](https://github.com/detekt/detekt/actions?query=workflow%3A%22Deploy+Snapshot%22) GitHub Action workflow. 

## Gradle setup with Buildscript

If you're using Gradle with the `buildscript` block, you should update your top level `build.gradle` file with:

```groovy
buildscript {
  repositories {
    maven {
      url "https://central.sonatype.com/repository/maven-snapshots/"
    }
  }
  dependencies {
    classpath "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:main-SNAPSHOT"
  }
}

apply plugin: "io.gitlab.arturbosch.detekt"

allprojects {
  repositories {
    maven {
      url "https://central.sonatype.com/repository/maven-snapshots/"
    }
  }
}
```

Make sure that you're adding the sonatype maven repository to both the `repositories{}` block **inside** the `buildscript{}` block and outside it.

## Gradle setup with Plugin block

If you're using the `plugins{}` block to apply detekt, you should update your `build.gradle` file to:

```groovy
plugins {
  id("io.gitlab.arturbosch.detekt") version "main-SNAPSHOT"
}

allprojects {
  repositories {
    maven {
      url "https://central.sonatype.com/repository/maven-snapshots/"
    }
  }
}
```

Plus you need to update the `settings.gradle` file as follows:

```groovy
pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "io.gitlab.arturbosch.detekt") {
                useModule("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${requested.version}")
            }
        }
    }
    repositories {
        // Your other repos here.
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}
```

Please note that the extra `resolutionStrategy{}` block is needed as we don't publish a Gradle Plugin marker for our snapshots.
