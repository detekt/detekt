# Migration Guide

### RC4 -> RC5

- Formatting rule set was removed. Use the `detektIdeaFormat` task, KtLint or wait for the official kotlin format 
tool which will be released soon (Hadi mentioned it in a reply to a tweet somewhere).
- McCabe calculation was corrected and can now be slightly higher which can result in unexpected `ComplexMethod` 
findings.

### RC3 -> RC4

- CatchXXX and ThrowXXX rules were reimplemented and combined into TooGenericExceptionCatched and 
TooGenericExceptionThrown rules. Own exceptions can be added to the list.
- EmptyXXXBlock rules were reimplemented and can be turned off individually 
- The rule NamingConventions was reimplemented and now every case is separately configurable and new cases were added

See [default-detekt-config.yaml](detekt-cli/src/main/resources/default-detekt-config.yaml)

### RC2 -> RC3

- MagicNumber rule has now different ignore properties

### RC1 -> RC2

- Make sure to upgrade! RC2 fixes a number of MagicNumber's issues 
and adds properties to make this rule more configurable.

### M13.1 -> RC1

- Attention: new `MagicNumber` and `ReturnCount` rules can let your CI fail
- Sample project now reflects all possible custom extensions to detekt, see `extensions` section in README
- `--output` points to a directory now. This is due the fact that many output reports can be generated at once
- Each `OutputReport` specifies a file name and ending. The parameter `--output-name` can be used to override the 
default provided file name of the `OutputReport`. Unnecessary output reports for your project can be turned off in 
the configuration.

### M13 -> M13.1

- Misspelled class `Dept` was renamed to `Debt`, if you using custom rule sets, please rebuild it
- CLI parameter `--project` was renamed to `--input` to match the input parameter of the gradle plugin

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
