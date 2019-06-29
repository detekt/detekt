---
title:  "Howto: using snapshot releases of detekt"
published: true
permalink: howto-snapshots.html
summary: "Use snapshot releases to test future detekt changes as soon as possible."
tags: [guides]
---

detekt uses [bintray](https://bintray.com/arturbosch/code-analysis/detekt) for releases and [artifactory](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/io/gitlab/arturbosch/detekt/detekt-cli/) for snapshots.
To configure snapshot usage in your gradle plugin apply the following changes to your build file:

```kotlin

detekt {
    // if a new release of detekt stays binary compatible with a previous
    // release, just change the 'toolVersion'
    toolVersion = "1.0.0-RC16-20190629.171442-3"
    config = files("$projectDir/detekt/config.yml")
    baseline = file("$projectDir/baseline.xml")

    reports {
        html {
            enabled = true
            destination = file("$rootDir/detekt.html")
        }
    }
}

// this changes may be necessary if detekt's core modules like 'cli, core, api or rules'
// change in a way that is not binary compatible to older releases
dependencies {
    // use the detekt configuration for only official detekt modules
    detekt "io.gitlab.arturbosch.detekt:detekt-cli:1.0.0-RC16-20190629.171442-3"
    detekt "io.gitlab.arturbosch.detekt:detekt-core:1.0.0-RC16-20190629.171442-3"
    detekt "io.gitlab.arturbosch.detekt:detekt-api:1.0.0-RC16-20190629.171442-3"
    ...
    // use detektPlugins for detekt plugins - custom or official like the formatting one
    detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:1.0.0-RC16-20190629.171442-3"
}
```
