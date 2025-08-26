---
id: reporting
title: "Reporting"
keywords: [reporting]
summary: This page describes each reporting format and explains how to leverage them.
sidebar_position: 4
---

## Formats

In addition to the CLI output, Detekt supports 4 different types of output reporting formats.
You can refer to [CLI](../gettingstarted/cli) or [Gradle](../gettingstarted/gradle) to find
out how to configure these report formats.

### TXT
Similar to the console output, each line of the txt output represents a finding and contains
finding signature to help edit [baseline files](../gettingstarted/gradle).

```
EmptyFunctionBlock - [This empty block of code can be removed.] at /user/home/detekt/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/DetektPlugin.kt:14:42 - Signature=DetektPlugin.kt$DetektPlugin${ }
NoUnusedImports - [Unused import] at /user/home/detekt/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/DetektPlugin.kt:9:1 - Signature=io.gitlab.arturbosch.detekt.DetektPlugin.kt:9
NoUnusedImports - [Unused import] at /user/home/detekt/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/DetektPlugin.kt:10:1 - Signature=io.gitlab.arturbosch.detekt.DetektPlugin.kt:10
NoConsecutiveBlankLines - [Needless blank line(s)] at /user/home/detekt/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/DetektPlugin.kt:86:1 - Signature=io.gitlab.arturbosch.detekt.DetektPlugin.kt:86
UnusedPrivateMember - [Private function registerDetektJvmTasks is unused.] at /user/home/detekt/detekt-gradle-plugin/src/main/kotlin/io/gitlab/arturbosch/detekt/DetektPlugin.kt:17:5 - Signature=DetektPlugin.kt$DetektPlugin$private fun Project.registerDetektJvmTasks(extension: DetektExtension)
```

### HTML
HTML is a human-readable format that can be open through browser. It includes different metrics
and complexity reports of this run, in addition to the findings with detailed descriptions and
report. Check out the example: ![HTML report](/img/tutorial/html.png)

### XML
XML is a machine-readable format that can be integrated with CI tools. It is compatible with
[Checkstyle](https://checkstyle.sourceforge.io/) output.

### SARIF
[SARIF](https://sarifweb.azurewebsites.net/) is a standard format for the output of 
static analysis tools. It is a JSON format with a defined 
[schema](https://docs.oasis-open.org/sarif/sarif/v2.0/csprd02/schemas/). It is currently supported
by GitHub Code Scanning, and we expect more consuming tools will adopt this format in the future.

### MD
Markdown is a lightweight markup language for creating formatted text using a plain-text editor.
The output structure looks similar to HTML format.
About [markdown](https://github.github.com/gfm/#what-is-markdown-) on GitHub.

## Severity
For machine-readable format, it is possible to configure the severity of each finding to fit
your CI policy with respects to errors. You may specify the severity level in the config file
for rule, or ruleSets:

```yaml
empty-blocks:
  active: true
  severity: error
  EmptyCatchBlock:
    active: true
    severity: info
```

The severity will be computed in the priority order:
- Severity of the rule if exists
- Severity of the parent ruleset if exists
- Default severity: warning

## Relative path
In a shared codebase, it is often required to use relative path so that all developers and tooling
have a consistent view. This can be enabled by CLI option `--base-path` or Gradle as the following:

```groovy
detekt {
    basePath = projectDir
}
```

Note that this option only affects file paths in those formats for machine consumers,
namely XML and SARIF.

## Merging reports

The machine-readable report formats support report merging.
Detekt Gradle Plugin is not opinionated in how merging is set up and respects each project's build logic, especially 
the merging makes most sense in a multi-module project. In this spirit, only Gradle tasks are provided.

At the moment, merging XML and SARIF are supported. You can refer to the sample build script below and 
run `./gradlew detekt reportMerge --continue` to execute detekt tasks and merge the corresponding reports.

### Groovy DSL
```groovy
task reportMerge(type: io.gitlab.arturbosch.detekt.report.ReportMergeTask) {
  output = project.layout.buildDirectory.file("reports/detekt/merge.xml") // or "reports/detekt/merge.sarif"
}

subprojects {
  detekt {
    reports.xml.required.set(true)
    // reports.sarif.required.set(true)
  }

  plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin) {
    tasks.withType(io.gitlab.arturbosch.detekt.Detekt) { detektTask ->
      finalizedBy(reportMerge)

      reportMerge.configure { mergeTask ->
        mergeTask.input.from(detektTask.xmlReportFile) // or detektTask.sarifReportFile
      }
    }
  }
}
```

### Kotlin DSL

```kotlin
val reportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) { 
  output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml")) // or "reports/detekt/merge.sarif"
}

subprojects {
  detekt {
    reports.xml.required.set(true)
    // reports.sarif.required.set(true)
  }
  
  plugins.withType(io.gitlab.arturbosch.detekt.DetektPlugin::class) {
    tasks.withType(io.gitlab.arturbosch.detekt.Detekt::class) detekt@{
      finalizedBy(reportMerge)

      reportMerge.configure {
        input.from(this@detekt.xmlReportFile) // or .sarifReportFile
      }
    }
  }
}
```

## Integration with Github Code Scanning
If your repository is hosted on Github, you can enable SARIF output in your repository.
You can follow to the [official documentation](https://docs.github.com/en/github/finding-security-vulnerabilities-and-errors-in-your-code/uploading-a-sarif-file-to-github).

You can follow the example below as a quick start:
```yaml
jobs:
  without-type-resolution:
    runs-on: ubuntu-latest
    env:
      GRADLE_OPTS: -Dorg.gradle.daemon=false
    steps:
      - name: Checkout Repo
        uses: actions/checkout@v3

      - name: Setup Java
        uses: actions/setup-java@v3
        with:
          java-version: 11

      - name: Run detekt
        run: ./gradlew detekt

      # Make sure we always run this upload task,
      # because the previous step may fail if there are findings.
      - name: Upload SARIF to Github using the upload-sarif action
        uses: github/codeql-action/upload-sarif@v1
        if: success() || failure()
        with:
          sarif_file: build/detekt.sarif
```
