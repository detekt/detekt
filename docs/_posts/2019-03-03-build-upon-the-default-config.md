---
title:  "Howto: build upon the default yaml config file"
published: true
permalink: howto-buildupondefaultconfig.html
summary: "Use the new feature to rely on the opinionated default yaml configuration file."
tags: [guides]
---

A common use case of _detekt_ users was to build upon the default config file and use a second config file to override
some defaults.
Speaking in _Gradle_ terms, this could look like following:
```gradle
detekt {
    ...
    config = files(
            project.rootDir.resolve("config/default-detekt-config.yml"),
            project.rootDir.resolve("config/our.yml")
    )
    baseline = project.rootDir.resolve("config/baseline.xml")
    ...
}
```

Starting from RC13, two new commandline flags got introduced:
- `--fail-fast`
- and `--build-upon-default-config`
- (`buildUponDefaultConfig` and `failFast` properties for gradle setup)

Both options allow us to use the distributed `default-detekt-config.yml` as the backup configuration when
no rule configuration is found in the specified configuration (`--config` or `config = ...`).  
This allows us to simplify our detekt setup without copy-pasting a huge config file:
```gradle
detekt {
    ...
    buildUponDefaultConfig = true
    config = files(project.rootDir.resolve("config/our.yml"))
    baseline = project.rootDir.resolve("config/baseline.xml")
    ...
}
```


{% include links.html %}
