---
title: "Using Snapshots"
keywords: snapshot releases 
sidebar: 
permalink: snapshots.html
summary: This page explains how you can setup snapshots for your Detekt build to test the latest unreleased features. 
---

This page explains how you can use our **latest snapshots** to test upcoming unreleased features.

## Where to download snapshots

You can find the latest snapshot on [sonatype](https://oss.sonatype.org/#view-repositories;snapshots~browsestorage~io/gitlab/arturbosch/detekt). A new snapshot is published after every merge to `main` from the [Deploy Snapshot](https://github.com/detekt/detekt/actions?query=workflow%3A%22Deploy+Snapshot%22) Github Action workflow. 

## Gradle setup with Buildscript

If you're using Gradle with the `buildscript` block, you should update your top level `build.gradle` file with:

```groovy
buildscript {
  repositories {
    maven {
      url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
  }
  dependencies {
    classpath "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:master-SNAPSHOT"
  }
}

apply plugin: "io.gitlab.arturbosch.detekt"

allprojects {
  repositories {
    maven {
      url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
  }
}
```

Make sure that you're adding the sonatype maven repository to both the `repositories{}` block **inside** the `buildscript{}` block and outside it.

## Gradle setup with Plugin block

If you're using the `plugins{}` block to apply detekt, you should update your `build.gradle` file to:

```groovy
plugins {
  id("io.gitlab.arturbosch.detekt") version "master-SNAPSHOT"
}

allprojects {
  repositories {
    maven {
      url "https://oss.sonatype.org/content/repositories/snapshots/"
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
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}
```

Please note that the extra `resolutionStrategy{}` block is needed as we don't publish a Gradle Plugin marker for our snapshots.
