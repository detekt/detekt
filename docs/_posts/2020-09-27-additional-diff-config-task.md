---
title:  "Recipe: diff your detekt config with the default one"
published: true
permalink: howto-diff-config.html
summary: "This guide shows how to create an additional Gradle task which generates the default detekt config."
tags: [guides]
---

detekt's `./gradlew detektGenerateConfig` task copies the default configuration file to the location
specified by the `config` property.

```kt
detekt {
  ...
  config = files(...)
  ...
}
```

When the file on this location already exists, your configuration won't be overwritten, and the task is a noop.

When we release a new version, some users like to generate the default one to compare changed properties.
This can be done by running the detekt cli with the `--generate-config --config [/new/location]` flags.
When already using Gradle, we can write a custom task and share this procedure with the team:

```kt
import io.gitlab.arturbosch.detekt.DetektGenerateConfigTask

val createDetektConfigForDiff by tasks.registering(DetektGenerateConfigTask::class) {
    description = "Generate newest default detekt config"
    config.setFrom(buildDir.resolve("detekt-diff.yaml"))

    doFirst {
      // optionally delete the old config diff file first 
    }
}
```

The last step involves calling your favorite diff tool (e.g. `diff detekt-diff.yaml my_config.yaml`) or using an online service like `http://incaseofstairs.com/jsdiff/`.

Likewise we can diff the default config of detekt version X with the default config of detekt version X-1. This will tell us which properties are new in version X. 
