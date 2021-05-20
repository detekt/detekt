---
title: "Compatibility Table"
keywords: detekt kotlin gradle compatibility android 
sidebar: 
permalink: compatibility.html
summary: This page lists the version of the Gradle plugins have been used to build Detekt.
---

When shipping the Detekt Gradle Plugin, we depend on both the **Kotlin Gradle Plugin** and the **Android Gradle Plugin**.

Those dependencies are applied as `compileOnly` ([see here](https://github.com/detekt/detekt/blob/75622d3ba88b0ae0357aec5f2d82a55aa6c6d157/detekt-gradle-plugin/build.gradle.kts#L17-L18)) to allow our users to pick the version of the Gradle plugin they prefer and don't impose the one we use inside detekt.

We try to support b**ackward compatibility** when possible, although that's not always trivial (especially with AGP or across minor versions of Kotlin).

This table lists the version of the Gradle plugin we used to compile the Detekt Gradle plugin. 

Consider **aligning** your Gradle plugin versions with the one listed below, as we can offer better support on Issues and Discussions for the listed versions of those tools.

| Detekt Version | Gradle Version | Kotlin Version | AGP Version |
| -------------- | -------------- | -------------- | ----------- |
| `1.17.0`       | `7.0.1`        | `1.4.32`       | `4.2.0`     | 
| `1.16.0`       | `6.8.0`        | `1.4.21`       | `4.1.2`     | 
| `1.15.0`       | `6.8.0`        | `1.4.10`       | `4.0.1`     | 
| `1.14.2`       | `6.7.0`        | `1.4.10`       | `4.0.1`     | 
| `1.14.0`       | `6.7-rc-2`     | `1.4.10`       | `4.0.1`     | 
| `1.13.1`       | `6.6.1`        | `1.4.0`        | `4.0.1`     | 

_(older versions are omitted for brevity)_
