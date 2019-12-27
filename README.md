# __detekt__

[![Join the chat at https://kotlinlang.slack.com/messages/C88E12QH4/convo/C0BQ5GZ0S-1511956674.000289/](https://img.shields.io/badge/chat-on_slack-red.svg?style=flat-square)](https://kotlinlang.slack.com/messages/C88E12QH4/convo/C0BQ5GZ0S-1511956674.000289/)
[![Visit the website at https://arturbosch.github.io/detekt/](https://img.shields.io/badge/visit-website-red.svg?style=flat-square)](https://arturbosch.github.io/detekt/)
[![Download](https://api.bintray.com/packages/arturbosch/code-analysis/detekt/images/download.svg) ](https://bintray.com/arturbosch/code-analysis/detekt/_latestVersion)
[![gradle plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/io/gitlab/arturbosch/detekt/io.gitlab.arturbosch.detekt.gradle.plugin/maven-metadata.xml.svg?label=Gradle&style=flat-square)](https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt)

[![build status](https://travis-ci.org/arturbosch/detekt.svg?branch=master)](https://travis-ci.org/arturbosch/detekt)
[![build status windows](https://ci.appveyor.com/api/projects/status/3q9g98vveiul7yut?svg=true)](https://ci.appveyor.com/project/arturbosch/detekt)
[![codecov](https://codecov.io/gh/arturbosch/detekt/branch/master/graph/badge.svg)](https://codecov.io/gh/arturbosch/detekt)
[![CodeFactor](https://www.codefactor.io/repository/github/arturbosch/detekt/badge)](https://www.codefactor.io/repository/github/arturbosch/detekt)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Farturbosch%2Fdetekt-intellij-plugin.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Farturbosch%2Fdetekt?ref=badge_shield)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

Meet _detekt_, a static code analysis tool for the _Kotlin_ programming language.
It operates on the abstract syntax tree provided by the Kotlin compiler.

![detekt in action](docs/images/detekt_in_action.png "detekt in action")

### Features

- Code smell analysis for your Kotlin projects
- Complexity reports based on lines of code, cyclomatic complexity and amount of code smells
- Highly configurable rule sets
- Suppression of findings with Kotlin's `@Suppress` and Java's `@SuppressWarnings` annotations
- Specification of quality gates which will break your build 
- Code Smell baseline and whitelisting for legacy projects
- [Gradle plugin](#with-gradle) for code analysis via Gradle builds
- Gradle tasks to use local `IntelliJ` distribution for formatting and inspecting Kotlin code
- [SonarQube integration](https://github.com/arturbosch/sonar-kotlin)
- Extensibility by enabling incorporation of personal rule sets, `FileProcessListener's` and `OutputReport's`
- [IntelliJ integration](https://github.com/arturbosch/detekt-intellij-plugin)
- Unofficial [Maven plugin](https://github.com/Ozsie/detekt-maven-plugin) by [Ozsie](https://github.com/Ozsie)

### Project Website

Visit [the project website](https://arturbosch.github.io/detekt/) for installation guides, release notes, migration guides, rule descriptions and configuration options.

#### Quick-Links

- [Changelog and migration guides](https://arturbosch.github.io/detekt/changelog.html)
- [Available CLI options](https://arturbosch.github.io/detekt/cli.html)
- [Rule set and rule descriptions](https://arturbosch.github.io/detekt/complexity.html)
- [Writing custom rules and extending detekt](https://arturbosch.github.io/detekt/extensions.html)
- [Suppressing issues in code](https://arturbosch.github.io/detekt/suppressing-rules.html)
- [Suppressing issues via baseline file](https://arturbosch.github.io/detekt/baseline.html)
- [Configuring detekt](https://arturbosch.github.io/detekt/configurations.html)
- Sample Gradle integrations examples:
    - [multi project (Kotlin DSL)](https://github.com/arturbosch/detekt/blob/master/build.gradle.kts)
    - [single project (Groovy DSL)](https://github.com/arturbosch/kutils/blob/master/build.gradle)
    - [single project (Unofficial Maven plugin)](https://github.com/arturbosch/sonar-kotlin/blob/master/pom.xml)
    - [setup additional detekt task for all modules (Kotlin DSL)](https://github.com/arturbosch/detekt/blob/3357abba87e1550c65b6610012bb291e0fbb64ce/build.gradle.kts#L280-L295)
    - [setup additional formatting task for all modules (Kotlin DSL)](https://github.com/arturbosch/detekt/blob/3357abba87e1550c65b6610012bb291e0fbb64ce/build.gradle.kts#L262-L278)

### Quick Start ...

#### with the command-line interface

```shell script
git clone https://github.com/arturbosch/detekt
cd detekt
./gradlew build shadowJar
java -jar detekt-cli/build/libs/detekt-cli-[version]-all.jar --help
```

#### with Gradle

Gradle 5.0+ is required:

```kotlin
buildscript {
    repositories {
        jcenter()
    }

    // or

    mavenCentral()
    jcenter {
        content {
            // just allow to include kotlinx projects
            // detekt needs 'kotlinx-html' for the html report
            includeGroup "org.jetbrains.kotlinx"
        }
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt").version("[version]")
}

detekt {
    failFast = true // fail build on any finding
    buildUponDefaultConfig = true // preconfigure defaults
    config = files("$projectDir/config/detekt.yml") // point to your custom config defining rules to run, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt

    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
    }
}

tasks {
    withType<Detekt> {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        this.jvmTarget = "1.8"
    }
}
```

See [bintray](https://bintray.com/arturbosch/code-analysis/detekt) for releases and [artifactory](https://oss.jfrog.org/artifactory/webapp/#/artifacts/browse/tree/General/oss-snapshot-local/io/gitlab/arturbosch/detekt/detekt-cli/) for snapshots.

### Adding more rule sets

detekt itself provides a wrapper over [KtLint](https://github.com/shyiko/ktlint) as a `formatting` rule set
which can be easily added to the Gradle configuration:

```kotlin
dependencies {
    detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:[version]"
}
```

Likewise custom [extensions](https://arturbosch.github.io/detekt/extensions.html) can be added to detekt.

### Contributors

If you contributed to detekt but your name is not in the list, please feel free to add yourself to it!

- [Artur Bosch](https://github.com/arturbosch) - Maintainer
- [Marvin Ramin](https://github.com/Mauin) - Collaborator, Bunch of rules, Active on Issues, refactorings, MultiRule
- [schalks](https://github.com/schalkms) - Collaborator, Active on Issues, Bunch of rules, Project metrics
- [Niklas Baudy](https://github.com/vanniktech) - Active on Issues, Bunch of rules, Bug fixes
- [lummax](https://github.com/lummax) - Cli enhancements
- [Svyatoslav Chatchenko](https://github.com/MyDogTom) - Active on Issues, NamingConventions and UnusedImport fixes
- [Sean Flanigan](https://github.com/seanf) - Config from classpath resource
- [Sebastian Schuberth](https://github.com/sschuberth) - Active on Issues, Windows support
- [Olivier Lemasle](https://github.com/olivierlemasle) - NP-Bugfix, rules fixes, Gradle plugin improvement
- [Marc Prengemann](https://github.com/winterDroid) - Support for custom output formats, prototyped Rule-Context-Issue separation
- [Sebastiano Poggi](https://github.com/rock3r) - Enhanced milestone report script, Magic number fixes
- [Ilya Tretyakov](https://github.com/jvilya) - Sonar runs should not auto correct formatting.
- [Andrey T](https://github.com/mr-procrastinator) - Readme fix
- [Ivan Balaksha](https://github.com/tagantroy) - Rules: UnsafeCast, SpreadOperator, UnsafeCallOnNullableType, LabeledExpression
- [Anna Y](https://github.com/Nevvea7) - Readme fix
- [Karol Wrótniak](https://github.com/koral--) - Treat comments as not empty blocks
- [Radim Vaculik](https://github.com/radimvaculik) - VariableMaxLength - bugfix
- [Martin Nonnenmacher](https://github.com/mnonnenmacher) - UndocumentedPublicClass - enum support
- [Dmytro Troynikov](https://github.com/DmytroTroynikov) - Updated Magic Number rule to ignore Named Arguments
- [Andrew Ochsner](https://github.com/aochsner) - Updated Readme for `failFast` option
- [Paul Merlin](https://github.com/eskatos) - Gradle build improvements
- [Konstantin Aksenov](https://github.com/vacxe) - Coding improvement
- [Matthew Haughton](https://github.com/3flex) - Added type resolution, Dependency updates, Coding + Documentation improvements
- [Janusz Bagiński](https://github.com/jbaginski) - Fixed line number reporting for MaxLineLengthRule 
- [Mike Kobit](https://github.com/mkobit) - Gradle build improvements
- [Philipp Hofmann](https://github.com/philipphofmann) - Readme improvements
- [Olivier PEREZ](https://github.com/olivierperez) - Fixed Typo in Readme
- [Sebastian Kaspari](https://github.com/pocmo) - Html-Output-Format, Documentation fix
- [Ilya Zorin](https://github.com/geralt-encore) - Rule improvement: UnnecessaryAbstractClass
- [Gesh Markov](https://github.com/markov) - Improve error message for incorrect configuration file
- [Patrick Pilch](https://github.com/patrickpilch) - Rule improvement: ReturnCount
- [Serj Lotutovici](https://github.com/serj-lotutovici) - Rule improvement: LongParameterList
- [Dmitry Primshyts](https://github.com/deeprim) - Rule improvement: MagicNumber
- [Egor Neliuba](https://github.com/egor-n) - Rule improvement: EmptyFunctionBlock, EmptyClassBlock
- [Said Tahsin Dane](https://github.com/tasomaniac/) - Gradle plugin improvements
- [Misa Torres](https://github.com/misaelmt) - Added: TrailingWhitespace and NoTabs rules
- [R.A. Porter](https://github.com/coyotesqrl) - Updated Readme links to RuleSets
- [Robbin Voortman](https://github.com/rvoortman) - Rule improvement: MaxLineLength
- [Mike Gorunov](http://github.com/Miha-x64/) — Rule improvement: UndocumentedPublicFunction
- [Joey Kaan](https://github.com/jkaan) - New rule: MandatoryBracesIfStatements
- [Dmitriy Samaryan](https://github.com/samarjan92) - Rule fix: SerialVersionUIDInSerializableClass
- [Mariano Simone](https://github.com/marianosimone) - Rule improvement: UnusedPrivateMember. New Rules: UnusedPrivateClass, VarCouldBeVal
- [Shunsuke Maeda](https://github.com/duck8823) - Fix: to work on multi module project using [maven plugin](https://github.com/Ozsie/detekt-maven-plugin)
- [Mikhail Levchenko](https://github.com/mishkun) - New rules: Unnecessary let, ExplicitItLambdaParameter
- [Scott Kennedy](https://github.com/scottkennedy) - Minor fixes
- [Mickele Moriconi](https://github.com/mickele) - Added: ConstructorParameterNaming and FunctionParameterNaming rules
- [Lukasz Jazgar](https://github.com/ljazgar) - Fixed configuring formatting rules
- [Pavlos-Petros Tournaris](https://github.com/pavlospt) - Lazy evaluation of Regex in Rules
- [Erhard Pointl](https://github.com/epeee) - Kotlin DSL and Gradle enhancements
- [Tyler Thrailkill](https://github.com/snowe2010) - FunctionNaming rule enhancements
- [Tarek Belkahia](https://github.com/tokou) - TooManyFunctions rule options
- [Bournane Abdelkrim](https://github.com/karimkod) - Fix typos
- [Rafael Toledo](https://github.com/rafaeltoledo) - Fix Gradle plugin badge
- [Alberto Ballano](https://github.com/aballano) - ExceptionRaisedInUnexpectedLocation rule improvements
- [Guido Pio Mariotti](https://github.com/gmariotti) - Documentation improvement
- [Mygod](https://github.com/Mygod) - UnusedImports rule improvement
- [Andreas Volkmann](https://github.com/AndreasVolkmann) - yaml code comments
- [glammers](https://github.com/glammers) - Documentation improvement
- [Ahmad El-Melegy](https://github.com/mlegy) - yaml syntax fix
- [Arjan Kleene](https://github.com/arjank) - Add unnecessary apply rule
- [Paweł Gajda](https://github.com/pawegio) - Rule improvement: FunctionParameterNaming
- [Alistair Sykes](https://github.com/alistairsykes) - Doc improvement
- [Andrew Arnott](https://github.com/andrew-arnott) - UnusedPrivateMember improvement
- [Tyler Wong](https://github.com/tylerbwong) - UnderscoresInNumericLiterals rule
- [Daniele Conti](https://github.com/fourlastor) - ObjectPropertyNaming improvement
- [Nicola Corti](https://github.com/cortinico) - Fixed Suppress of MaxLineLenght
- [Michael Lotkowski](https://github.com/DownMoney) - Rule improvement: False positive UnusedImport for componentN
- [Nuno Caro](https://github.com/Pak3nuh) - Adds TXT report support on Gradle plugin
- [Minsuk Eom](https://github.com/boxresin) - Rule fix: PackageNaming

### Mentions

[![androidweekly](https://img.shields.io/badge/androidweekly.net-259-orange.svg?style=flat-square)](http://androidweekly.net/issues/issue-259) 
[![androidweekly](https://img.shields.io/badge/androidweekly.cn-154-orange.svg?style=flat-square)](http://androidweekly.cn/android-dev-wekly-issue-154/)

As mentioned in...

- [SBCARS '18 -  Are you still smelling it?: A comparative study between Java and Kotlin language](https://doi.org/10.1145/3267183.3267186) by [Flauzino et al.](https://github.com/matheusflauzino/smells-experiment-Kotlin-and-Java)
- [KotlinConf 2018 - Safe(r) Kotlin Code - Static Analysis Tools for Kotlin by Marvin Ramin](https://www.youtube.com/watch?v=yjhQiP0329M)
- [droidcon NYC 2018 - Static Code Analysis For Kotlin](https://www.youtube.com/watch?v=LT6m5_LO2DQ)
- Kotlin on Code Quality Tools - by @vanniktech [Slides](https://docs.google.com/presentation/d/1sUoQCRHTR01JfaS67Qkd7K1rdRLOhO6QGCelZZwxOKs/edit) [Presentation](https://www.youtube.com/watch?v=FKDNE6PPTTE)
- [@medium/acerezoluna/static-code-analysis-tools-for-kotlin-in-android](https://medium.com/@acerezoluna/static-code-analysis-tools-for-kotlin-in-android-fa072125fd50)
- [@medium/annayan/writing-custom-lint-rules-for-your-kotlin-project-with-detekt](https://proandroiddev.com/writing-custom-lint-rules-for-your-kotlin-project-with-detekt-653e4dbbe8b9)
- [Free Continuous Integration for modern Android apps with CircleCI](https://tips.seebrock3r.me/free-continuous-integration-for-modern-android-apps-with-circleci-940e33451c83)
- [Static code analysis for Kotlin in Android](https://blog.thefuntasty.com/static-code-analysis-for-kotlin-in-android-8676c8d6a3c5)
- [The Art of Android DevOps](https://blog.undabot.com/the-art-of-android-devops-fa29396bc9ee)
- [Android Basics: Continuous Integration](https://academy.realm.io/posts/360-andev-2017-mark-scheel-continuous-integration-android/)
- [Kotlin Static Analysis — why and how?](https://proandroiddev.com/kotlin-static-analysis-why-and-how-a12042e34a98)
- [Check the quality of Kotlin code](https://blog.frankel.ch/check-quality-kotlin-code/)

Integrations:

- [SonarKotlin](https://docs.sonarqube.org/display/PLUG/SonarKotlin)
- [Codacy](https://www.codacy.com)
- [Gradle plugin that generates ErrorProne, Findbugs, Checkstyle, PMD, CPD, Lint, Detekt & Ktlint Tasks for every subproject](https://github.com/vanniktech/gradle-code-quality-tools-plugin)
- [Java library for parsing report files from static code analysis](https://github.com/tomasbjerre/violations-lib)
- [sputnik is a free tool for static code review and provides support for detekt](https://github.com/TouK/sputnik)
- [Novoda Gradle Static Analysis plugin](https://github.com/novoda/gradle-static-analysis-plugin)
- [Maven plugin that wraps the Detekt CLI](https://github.com/Ozsie/detekt-maven-plugin)
- [Gradle plugin that helps facilitate GitHub PR checking and automatic commenting of violations](https://github.com/btkelly/gnag)
- [Codefactor](http://codefactor.io/)

#### Credits

- [JetBrains](https://github.com/jetbrains/) - Creating IntelliJ + Kotlin
- [PMD](https://github.com/pmd/pmd) & [Checkstyle](https://github.com/checkstyle/checkstyle) & [KtLint](https://github.com/shyiko/ktlint) - Ideas for threshold values and style rules
