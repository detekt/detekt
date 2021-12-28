# __detekt__

[![Join the chat at #detekt on KotlinLang](https://img.shields.io/badge/%23detekt-on_slack-red.svg?logo=slack)](https://kotlinlang.slack.com/archives/C88E12QH4)
[![Visit the website at detekt.github.io/detekt/](https://img.shields.io/badge/visit-website-red.svg?logo=firefox)](https://detekt.github.io/detekt/)
[![Maven Central](https://img.shields.io/maven-central/v/io.gitlab.arturbosch.detekt/detekt-cli?label=MavenCentral&logo=apache-maven)](https://search.maven.org/artifact/io.gitlab.arturbosch.detekt/detekt-cli)
[![Gradle Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/gitlab/arturbosch/detekt/io.gitlab.arturbosch.detekt.gradle.plugin/maven-metadata.xml.svg?label=Gradle&logo=gradle)](https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt)

![Pre Merge Checks](https://github.com/detekt/detekt/workflows/Pre%20Merge%20Checks/badge.svg?branch=main)
[![Codecov](https://codecov.io/gh/detekt/detekt/branch/main/graph/badge.svg)](https://codecov.io/gh/detekt/detekt)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)
[![FOSSA Status](https://app.fossa.com/api/projects/custom%2B25591%2Fgithub.com%2Fdetekt%2Fdetekt.svg?type=small)](https://app.fossa.com/projects/custom%2B25591%2Fgithub.com%2Fdetekt%2Fdetekt?ref=badge_small)

Meet _detekt_, a static code analysis tool for the _Kotlin_ programming language.
It operates on the abstract syntax tree provided by the Kotlin compiler.

![detekt in action](docs/images/detekt_in_action.png "detekt in action")

### Features

- Code smell analysis for your Kotlin projects
- Complexity reports based on lines of code, cyclomatic complexity and amount of code smells
- Highly configurable rule sets
- Suppression of findings with Kotlin's `@Suppress` and Java's `@SuppressWarnings` annotations
- Specification of quality gates which will break your build
- Code Smell baseline and suppression for legacy projects
- [Gradle plugin](#with-gradle) for code analysis via Gradle builds
- [SonarQube integration](https://github.com/detekt/sonar-kotlin)
- Extensibility by enabling incorporation of personal rule sets, `FileProcessListener's` and `OutputReport's`
- [IntelliJ integration](https://github.com/detekt/detekt-intellij-plugin)
- Third party integrations for [Maven](https://github.com/Ozsie/detekt-maven-plugin), [Bazel](https://github.com/buildfoundation/bazel_rules_detekt/) and Github Actions ([Docker based](https://github.com/marketplace/actions/detekt-all) and [Javascript based](https://github.com/marketplace/actions/setup-detekt))

### Project Website

Visit [the project website](https://detekt.github.io/detekt/) for installation guides, release notes, migration guides, rule descriptions and configuration options.

#### Quick-Links

- [Changelog and migration guides](https://detekt.github.io/detekt/changelog.html)
- [Available CLI options](https://detekt.github.io/detekt/cli.html)
- [Rule set and rule descriptions](https://detekt.github.io/detekt/complexity.html)
- [Writing custom rules and extending detekt](https://detekt.github.io/detekt/extensions.html)
- [Suppressing issues in code](https://detekt.github.io/detekt/suppressing-rules.html)
- [Suppressing issues via baseline file](https://detekt.github.io/detekt/baseline.html)
- [Configuring detekt](https://detekt.github.io/detekt/configurations.html)
- Sample Gradle integrations examples:
    - [multi project (Kotlin DSL) with precompiled script plugin](https://github.com/detekt/detekt/blob/main/build-logic/src/main/kotlin/detekt.gradle.kts)
    - [single project (Groovy DSL)](https://github.com/arturbosch/kutils/blob/main/build.gradle)
    - [single project (Unofficial Maven plugin)](https://github.com/detekt/sonar-kotlin/blob/main/pom.xml)
    - [setup additional detekt task for all modules (Kotlin DSL)](https://github.com/detekt/detekt/blob/3357abba87e1550c65b6610012bb291e0fbb64ce/build.gradle.kts#L280-L295)
    - [setup additional formatting task for all modules (Kotlin DSL)](https://github.com/detekt/detekt/blob/3357abba87e1550c65b6610012bb291e0fbb64ce/build.gradle.kts#L262-L278)

### Quick Start ...

#### with the command-line interface

```sh
curl -sSLO https://github.com/detekt/detekt/releases/download/v[version]/detekt-cli-[version]-all.jar
java -jar detekt-cli-[version]-all.jar --help
```

You can find [other ways to install detekt here](https://detekt.github.io/detekt/cli.html)

#### with Gradle

```kotlin
plugins {
    id("io.gitlab.arturbosch.detekt").version("[version]")
}

repositories {
    mavenCentral()
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config = files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true) // observe findings in your browser with structure and code snippets
        xml.required.set(true) // checkstyle like format mainly for integrations like Jenkins
        txt.required.set(true) // similar to the console output, contains issue signature to manually edit baseline files
        sarif.required.set(true) // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
    }
}

// Groovy DSL
tasks.withType(Detekt).configureEach {
    jvmTarget = "1.8"
}
tasks.withType(DetektCreateBaselineTask).configureEach {
    jvmTarget = "1.8"
}

// or

// Kotlin DSL
tasks.withType<Detekt>().configureEach {
    jvmTarget = "1.8"
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = "1.8"
}
```

See [maven central](https://search.maven.org/artifact/io.gitlab.arturbosch.detekt/detekt-cli) for releases and [sonatype](https://oss.sonatype.org/#view-repositories;snapshots~browsestorage~io/gitlab/arturbosch/detekt) for snapshots.

If you want to use a SNAPSHOT version, you can find more info on [this documentation page](https://detekt.github.io/detekt/snapshots.html).

#### Requirements

Gradle 6.1+ is the minimum requirement. However, the recommended versions together with the other tools recommended versions are:

| Detekt Version | Gradle | Kotlin | AGP | Java Target Level | JDK Max Version |
| -------------- | ------ | ------ | --- | ----------------- | --------------- |
| `1.19.0`       | `7.3.0` | `1.5.31` | `4.2.2`| `1.8`       | `17`            |

The list of [recommended versions for previous detekt version is listed here](https://detekt.github.io/detekt/compatibility.html).

### Adding more rule sets

detekt itself provides a wrapper over [ktlint](https://github.com/pinterest/ktlint) as a `formatting` rule set
which can be easily added to the Gradle configuration:

```kotlin
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:[version]")
}
```

Likewise custom [extensions](https://detekt.github.io/detekt/extensions.html) can be added to detekt.

### Contributing

See [CONTRIBUTING](.github/CONTRIBUTING.md)

Thanks to all the people who contributed to detekt!

<a href="https://github.com/detekt/detekt/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=detekt/detekt" />
</a>

### Mentions

[![androidweekly](https://img.shields.io/badge/androidweekly.net-259-orange.svg?style=flat-square)](http://androidweekly.net/issues/issue-259)
[![androidweekly](https://img.shields.io/badge/androidweekly.cn-154-orange.svg?style=flat-square)](http://androidweekly.cn/android-dev-wekly-issue-154/)

As mentioned in...

- [KotlinConf 2018 - Safe(r) Kotlin Code - Static Analysis Tools for Kotlin by Marvin Ramin](https://www.youtube.com/watch?v=yjhQiP0329M)
- [droidcon NYC 2018 - Static Code Analysis For Kotlin](https://www.youtube.com/watch?v=LT6m5_LO2DQ)
- Kotlin on Code Quality Tools - by @vanniktech [Slides](https://docs.google.com/presentation/d/1sUoQCRHTR01JfaS67Qkd7K1rdRLOhO6QGCelZZwxOKs/edit) [Presentation](https://www.youtube.com/watch?v=FKDNE6PPTTE)
- [Integrating detekt in the Workflow](https://www.raywenderlich.com/24470020-integrating-detekt-in-the-workflow)
- [Check the quality of Kotlin code](https://blog.frankel.ch/check-quality-kotlin-code/)
- [Kotlin Static Analysis Tools](http://smyachenkov.com/posts/kotlin-static-analysis-tools/)
- [Are you still smelling it?: A comparative study between Java and Kotlin language](https://doi.org/10.1145/3267183.3267186) by [Flauzino et al.](https://github.com/matheusflauzino/smells-experiment-Kotlin-and-Java)
- [Preventing software antipatterns with Detekt](https://galler.dev/preventing-software-antipatterns-with-detekt/)

Integrations:

- [Codacy](https://www.codacy.com)
- [Gradle plugin that configures Error Prone, Checkstyle, PMD, CPD, Lint, Detekt & Ktlint](https://github.com/vanniktech/gradle-code-quality-tools-plugin)
- [Violations Lib](https://github.com/tomasbjerre/violations-lib) is a Java library for parsing report files like static code analysis.
- [sputnik](https://github.com/TouK/sputnik) is a free tool for static code review and provides support for detekt
- [Novoda Gradle Static Analysis plugin](https://github.com/novoda/gradle-static-analysis-plugin)
- [Detekt Maven plugin](https://github.com/Ozsie/detekt-maven-plugin) that wraps the Detekt CLI
- [Detekt Bazel plugin](https://github.com/buildfoundation/bazel_rules_detekt) that wraps the Detekt CLI
- [Gradle plugin that helps facilitate GitHub PR checking and automatic commenting of violations](https://github.com/btkelly/gnag)
- [Codefactor](http://codefactor.io/)
- [GitHub Action: Detekt All](https://github.com/marketplace/actions/detekt-all)
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- [MuseDev](https://github.com/marketplace/muse-dev)

Custom rules and reports from 3rd parties:

- [detekt-verify-implementation](https://github.com/cph-cachet/detekt-verify-implementation) by cph-cachet
- [detekt-hint](https://github.com/mkohm/detekt-hint) by mkohm is a plugin to detekt that provides detection of design principle violations through integration with Danger
- [GitLab report format](https://gitlab.com/cromefire_/detekt-gitlab-report)

#### Credits

- [JetBrains](https://github.com/jetbrains/) - Creating IntelliJ + Kotlin
- [PMD](https://github.com/pmd/pmd) & [Checkstyle](https://github.com/checkstyle/checkstyle) & [ktlint](https://github.com/pinterest/ktlint) - Ideas for threshold values and style rules
