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
    - [multi project (Kotlin DSL) with precompiled script plugin](https://github.com/detekt/detekt/blob/main/buildSrc/src/main/kotlin/detekt.gradle.kts)
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

    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
        sarif.enabled = true // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations with Github Code Scanning
    }
}


// Groovy DSL
tasks.withType(Detekt).configureEach {
    jvmTarget = "1.8"
}

// or

// Kotlin DSL
tasks.withType<Detekt>().configureEach {
    // Target version of the generated JVM bytecode. It is used for type resolution.
    jvmTarget = "1.8"
}
```

See [maven central](https://search.maven.org/artifact/io.gitlab.arturbosch.detekt/detekt-cli) for releases and [sonatype](https://oss.sonatype.org/#view-repositories;snapshots~browsestorage~io/gitlab/arturbosch/detekt) for snapshots.

If you want to use a SNAPSHOT version, you can find more info on [this documentation page](https://detekt.github.io/detekt/snapshots.html).

#### Requirements

Gradle 6.1+ is the minimum requirement. However, the recommended versions together with the other tools recommended versions are:

| Detekt Version | Gradle | Kotlin | AGP | Java Target Level | JDK Max Version |
| -------------- | ------ | ------ | --- | ----------------- | --------------- |
| `1.17.0` | `7.0.1` | `1.4.32` | `4.2.0` | `1.8` | `15` |

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
- [Sebastiano Poggi](https://github.com/rock3r) - Build tooling improvements, rules improvements and fixes, docs fixes, Gradle plugin improvements
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
- [Jonas Alves](https://github.com/jonasfa) - Rule fix: MagicNumber with ignoreNamedArgument and a negative value
- [Natig Babayev](https://github.com/natiginfo) - Readme improvements
- [David Phillips](https://github.com/daphil19) - New rule: MandatoryBracesLoops
- [Volkan Şahin](https://github.com/volsahin) - Documentation improvement
- [Remco Mokveld](https://github.com/remcomokveld) - Rename Blacklist/Whitelist to more meaningful names
- [Zachary Moore](https://github.com/zsmoore) - Rule, cli, gradle plugin, and config improvements
- [Veyndan Stuart](https://github.com/veyndan) - New rule: UseEmptyCounterpart; Rule improvement: UselessCallOnNotNull
- [Parimatch Tech](https://github.com/parimatch-tech) - New rule: LibraryEntitiesShouldNotBePublic, UnnecessaryFilter
- [Chao Zhang](https://github.com/chao2zhang) - SARIF report format; Rule improvements
- [Marcelo Hernandez](https://github.com/mhernand40) - New rule: SuspendFunWithFlowReturnType, ObjectExtendsThrowable
- [Harold Martin](https://github.com/hbmartin) - Rule improvement: ClassOrdering
- [Roman Ivanov](https://github.com/rwqwr) - Rule improvement: ReturnFromFinally
- [Severn Everett](https://github.com/severn-everett) - New rule: SleepInsteadOfDelay
- [Adam Kobor](https://github.com/adamkobor) - New rule: MultilineLambdaItParameter
- [Slawomir Czerwinski](https://github.com/sczerwinski) - Rule improvement: FunctionOnlyReturningConstant
- [Ivo Smid](https://github.com/bedla) - Fix Local development on Windows
- [Krzysztof Kruczynski](https://github.com/krzykrucz) - Rule fix: ThrowingExceptionInMain, ExitOutsideMain
- [Paya Do](https://github.com/payathedo) - Designer for Detekt's logo
- [zmunm](https://github.com/zmunm) - New rule: ObjectLiteralToLambda
- [Vinicius Montes Munhoz](https://github.com/vfmunhoz) - Documentation improvement
- [Eliezer Graber](https://github.com/eygraber) - Rule fix: ModifierOrder
- [Dominik Labuda](https://github.com/Dominick1993) - Gradle plugin improvement
- [Andre Paz](https://github.com/andrepaz) - Rule improvement: LongParameterList

### Mentions

[![androidweekly](https://img.shields.io/badge/androidweekly.net-259-orange.svg?style=flat-square)](http://androidweekly.net/issues/issue-259) 
[![androidweekly](https://img.shields.io/badge/androidweekly.cn-154-orange.svg?style=flat-square)](http://androidweekly.cn/android-dev-wekly-issue-154/)

As mentioned in...

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
- [Kotlin Static Analysis Tools](http://smyachenkov.com/posts/kotlin-static-analysis-tools/)
- [Speeding up the detekt task in a multi-project Gradle build](https://madhead.me/posts/detekt-faster/) (for detekt < 1.7.0)
- [SBCARS '18 -  Are you still smelling it?: A comparative study between Java and Kotlin language](https://doi.org/10.1145/3267183.3267186) by [Flauzino et al.](https://github.com/matheusflauzino/smells-experiment-Kotlin-and-Java)
- [Preventing software antipatterns with Detekt](https://galler.dev/preventing-software-antipatterns-with-detekt/)

Integrations:

- [SonarKotlin](https://docs.sonarqube.org/display/PLUG/SonarKotlin)
- [Codacy](https://www.codacy.com)
- [Gradle plugin that generates ErrorProne, Findbugs, Checkstyle, PMD, CPD, Lint, Detekt & Ktlint Tasks for every subproject](https://github.com/vanniktech/gradle-code-quality-tools-plugin)
- [Java library for parsing report files from static code analysis](https://github.com/tomasbjerre/violations-lib)
- [sputnik is a free tool for static code review and provides support for detekt](https://github.com/TouK/sputnik)
- [Novoda Gradle Static Analysis plugin](https://github.com/novoda/gradle-static-analysis-plugin)
- [Maven plugin that wraps the Detekt CLI](https://github.com/Ozsie/detekt-maven-plugin)
- [Bazel plugin that wraps the Detekt CLI](https://github.com/buildfoundation/bazel_rules_detekt)
- [Gradle plugin that helps facilitate GitHub PR checking and automatic commenting of violations](https://github.com/btkelly/gnag)
- [Codefactor](http://codefactor.io/)
- [GitHub Action: Detekt All](https://github.com/marketplace/actions/detekt-all)
- [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- [MuseDev](https://github.com/marketplace/muse-dev)

Custom rules and reports from 3rd parties:

- [cph-cachet/detekt-verify-implementation](https://github.com/cph-cachet/detekt-verify-implementation)
- [detekt-hint is a plugin to detekt that provides detection of design principle violations through integration with Danger](https://github.com/mkohm/detekt-hint)
- [GitLab report format](https://gitlab.com/cromefire_/detekt-gitlab-report)

#### Credits

- [JetBrains](https://github.com/jetbrains/) - Creating IntelliJ + Kotlin
- [PMD](https://github.com/pmd/pmd) & [Checkstyle](https://github.com/checkstyle/checkstyle) & [ktlint](https://github.com/pinterest/ktlint) - Ideas for threshold values and style rules
