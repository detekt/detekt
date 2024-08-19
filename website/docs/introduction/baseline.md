---
id: baseline
title: "Code Smell Baseline"
keywords: [baseline, suppressing, smells]
sidebar_position: 7
---

With the cli option `--baseline` or the detekt-gradle-plugin closure-property `baseline` you can specify a file which is used to generate a `baseline.xml`.
It is a file where ignored code smells are defined.

The intention of `CurrentIssues` is that only new code smells are printed on further analysis.
The `ManuallySuppressedIssues` can be used to write down false positive detections (instead of suppressing them and pollute your code base).

The `ID` node has the following structure: `<RuleID>:<Codesmell_Signature>`.  
When adding a custom issue to the xml file, make sure the `RuleID` should be self-explaining.

```xml
<SmellBaseline>
  <ManuallySuppressedIssues>
    <ID>CatchRuntimeException:Junk.kt$e: RuntimeException</ID>
  </ManuallySuppressedIssues>
  <CurrentIssues>
    <ID>NestedBlockDepth:Indentation.kt$Indentation$override fun procedure(node: ASTNode)</ID>
    <ID>TooManyFunctions:LargeClass.kt$io.gitlab.arturbosch.detekt.rules.complexity.LargeClass.kt</ID>
    <ID>ComplexMethod:DetektExtension.kt$DetektExtension$fun convertToArguments(): MutableList&lt;String&gt;</ID>
  </CurrentIssues>
</SmellBaseline>
```

#### CLI
To generate yourself a `baseline.xml` you need to provide the same config as the the rules you are going to scan your project.

```diff
java -jar detekt-cli-all.jar \
  --plugins detekt-formatting.jar \
  --build-upon-default-config \
  --config path/to/config/detekt/detekt.yml \
+ --baseline path/to/new/config/detekt/baseline.xml \
+ --create-baseline

#### Gradle

If you are using the gradle-plugin run the `detektBaseline` task to generate yourself a `baseline.xml`.
This will create one baseline file per Gradle module.
As this might not be the desired behavior for a multi module project, think about implementing
a custom meta baseline task:

###### Groovy DSL
```groovy
subprojects {
    detekt {
        // ...
        baseline = file("${rootProject.projectDir}/config/baseline.xml")
        // ...
    }
}

task detektProjectBaseline(type: io.gitlab.arturbosch.detekt.DetektCreateBaselineTask) {
    description = "Overrides current baseline."
    ignoreFailures.set(true)
    parallel.set(true)
    buildUponDefaultConfig.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
}
```

###### Kotlin DSL
```kotlin
subprojects {
    detekt {
        // ...
        baseline.set(file("${rootProject.projectDir}/config/baseline.xml"))
        // ...
    }
}

val detektProjectBaseline by tasks.registering(DetektCreateBaselineTask::class) {
    description = "Overrides current baseline."
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(true)
    parallel.set(true)
    setSource(files(rootDir))
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    baseline.set(file("$rootDir/config/detekt/baseline.xml"))
    include("**/*.kt")
    include("**/*.kts")
    exclude("**/resources/**")
    exclude("**/build/**")
}
```

#### FAQ

Be aware that auto formatting cannot be combined with the `baseline`.
The signatures for a `;` for example would be too ambiguous.
