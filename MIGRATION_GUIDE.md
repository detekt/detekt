# Migration Guide

### M11 -> M12

##### CLI

- No break just extra notification that you can pass now more than one configuration file within the `--config` and `--config-resource` parameters

This allows overriding certain configuration parameters in the base configuration (left-most config)

##### Gradle Plugin

- the detekt extension is now aware of `configuration profiles`
- non default or 'main' profile, needs to be specified like `gradle detektCheck -Ddetekt.profile=[profile-name]`

Instead of writing something like

```groovy
detekt {
    version = "1.0.0.M11"
    input = "$project.projectDir/src"
    filters = '.*/test/.*'
    config = "$project.projectDir/detekt-config.yml"
    output = "$project.projectDir/output.xml"
    idea {
        path = "$USER_HOME/.idea"
        codeStyleScheme = "$USER_HOME/.idea/idea-code-style.xml"
        inspectionsProfile = "$USER_HOME/.idea/inspect.xml"
        mask = "*.kt,"
    }
}
```

you have to put a `profile`-closure around the parameters

```groovy
detekt {
    profile("main") {
        version = "1.0.0.M11"
        input = "$project.projectDir/src"
        filters = '.*/test/.*'
        config = "$project.projectDir/detekt-config.yml"
        output = "$project.projectDir/output.xml"
    }
    profile("test") {
        filters = ".*/src/main/kotlin/.*"
        config = "$project.projectDir/detekt-test-config.yml"
    }
    idea {
        path = "$USER_HOME/.idea"
        codeStyleScheme = "$USER_HOME/.idea/idea-code-style.xml"
        inspectionsProfile = "$USER_HOME/.idea/inspect.xml"
        mask = "*.kt,"
    }
}
```

This allows you too configure `detekt-rules` specific for each module. Also allowing to have different configurations for production or test code.

##### Renamings

- `NoDocOverPublicClass` -> `UndocumentedPublicClass`
- `NoDocOverPublicMethod` -> `UndocumentedPublicFunction`

Rename this id's in your configuration

### M10 -> M11

- `detekt` task was renamed to `detektCheck` (gradle-plugin)

##### Renamings
- `empty` -> `empty-blocks`

### M9 -> M10

- `code-smell` rule set was renamed to `complexity` rule set (config)