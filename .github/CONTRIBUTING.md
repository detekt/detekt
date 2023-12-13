# Contributing to detekt

- Read [this article](https://chris.beams.io/posts/git-commit/) before writing commit messages.
- Before running any tests, run `./gradlew publishToMavenLocal` to ensure that all tests are using the locally built artifact (c.f. issue [#6708](https://github.com/detekt/detekt/issues/6708) and PR [#6415](https://github.com/detekt/detekt/pull/6415)). Otherwise, the tests will pull a possibly outdated version of detekt from Maven Central or Sonatype.
- Use `gradle build -x dokkaHtml` to build the source but exclude documentation JAR generation to save time.
- Make sure that `gradle detekt` does not report any errors.
- This repository follows the [Kotlin coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html),
  which are enforced by ktlint when running `gradle detekt`.
- Make sure your IDE uses [ktlint](https://github.com/pinterest/ktlint) formatting rules as well
  as the settings in [.editorconfig](../.editorconfig).
- We use [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) for testing. Please use the `Spec.kt` suffix on
  new test classes. If your new rule requires type resolution (i.e. it utilises `BindingContext`) then annotate your
  test class with `@KotlinCoreEnvironmentTest` and have the test class accept `KotlinCoreEnvironment` as a parameter.
  See "Testing a rule that uses type resolution" section of the [Using Type Resolution](../website/docs/gettingstarted/type-resolution.md)
  guide for details.
- The code in `detekt-api` and any rule in `detekt-rules` must be documented. We generate documentation for our website based on these modules.
- If some Kotlin code in `resources` folder (like `detekt-formatting`) shows a compilation error, right click on it and use `Mark as plain text`.

## Implementing new rules

When implementing a new rule, do not forget to perform the following steps:
- Add the new rule to a `RuleSetProvider` (such as `StyleGuideProvider`).
- Test the new rule and/or add tests for any changes made to a rule. Run detekt on itself and other
  Kotlin projects with the `--run-rule RuleSet:RuleId` option to test your rule in isolation. Make use of
  the `scripts/get_analysis_projects.kts` script to automatically establish a set of analysis projects.
- Run `./gradlew generateDocumentation` to add your rule and its config options to the `default-detekt-config.yml`.
- Run `./gradlew build`. This will execute tests locally.

To view the AST (PSI) of your source code, you can use the [PSI Viewer plugin](https://plugins.jetbrains.com/plugin/227-psiviewer) for IntelliJ.
This can be helpful when implementing and debugging rules.

The general policy regarding contributed rules is as follows:
- PRs will stay open for at least two days so that other users can give feedback.
- After some time and testing, there is a chance the contributed rule will become active by default.

In order for your PR to get accepted, it is important that you add
suitable **annotations** and provide all required types of **descriptions**.
The following two subsections describe how to
properly annotate a rule and how to compose the different types
of descriptions, respectively.

### Rule annotations

```kotlin
@ActiveByDefault(since = "1.0.0")
@RequiresTypeResolution
class SomeRule(config: Config = Config.empty) : Rule(config) {

    @Configuration("This is the description for the configuration parameter below.")
    private val name: String by config(default = "whatever should be the default")

}
```

Use the `@Configuration` annotation in combination with the `config` delegate to create
a configurable property for your rule. The name of the property will become the key and
the provided default will be the value in the `default-detekt-config.yml`. All information
are also used to generate the rule documentation in the wiki.
Note that a property that is marked with `@Configuration` must use the config
delegate (and vice versa).

Rules annotated with `@ActiveByDefault` will be marked as active in the `default-detekt-config.yml`.
Generally, this will not be the case for new rules.

A rule that requires type resolution must be marked with `@RequiresTypeResolution`.
See [the type resolution wiki page](../website/docs/gettingstarted/type-resolution.md) for
more detail on this topic.

The rule defined above will translate to a rule entry in the `default-detekt-config.yml`:
```yml
SomeRule:
    active: true
    name: 'whatever should be the default'
```

### Rule descriptions

A rule incorporated into the detekt framework is generally accompanied by three
types of descriptions:
1. **Documentation string**: Using conventional
   Javadoc/KDoc syntax, the documentation string is associated with the `Rule`
   subclass implementing the considered rule. Documentation strings associated
   with built-in rules are automatically pulled from the detekt codebase and
   used to generate the rule set overview available on the
   [detekt website](https://detekt.dev).
2. **Issue description**: The issue description gives a summary of the code
   smells that the respective rule is supposed to detect. From an implementation
   point of view, it is the string that `Rule` subclasses pass to the
   `description` parameter of the [`Issue` class][1]. In generated reports, the
   issue description is often used to introduce a list of code smells that the
   respective rule has identified.
3. **Code smell messages**: A code smell message is issued for every code
   smell (or *violation*) identified within the codebase. Implementation-wise,
   such a message is dynamically created during the construction of a
   [`CodeSmell` instance][2]. In generated reports, these messages are listed
   underneath the issue description. Built-in console reports such as
   `LiteFindingsReport` use these messages to display identified code smells to
   the user.

It is important that you provide a documentation string, an issue description,
and a message for each code smell that the new rule generates. When authoring
these descriptions, you should keep two different target audiences in mind:
1. The **documentation string** is generally read by individuals who want to
   learn about the *rule itself*. They might need to compose a detekt
   configuration for their codebase, need to understand what the rule is
   currently checking for in order to extend it, or are just interested in the
   available detekt rule sets.
2. The **issue description** as well as **code smell messages** are presented to
   developers whose codebase violates one or more rules that detekt checked for.
   This audience is generally less interested in the rule itself. Instead,
   individuals reading these texts will usually expect specific references to
   *their codebase* and what can be done in order to improve it.


#### Contents and structure of documentation (KDoc) strings

The description should be as detailed as possible as it will act as the
documentation of the rule. Incorporate the documentation string as KDoc
comment applied to the `Rule` subclass.

Make use of `<noncompliant>` and `<compliant>` blocks to add
non-compliant and compliant code examples. Add these blocks right after
the detailed description of the rule.

```kotlin
/**
 * Summary of the violation that this rule is concerned with,
 * potentially extended by a brief description on why it is
 * bad practice and what is usually done to eliminate it.
 *
 * Add more details if applicable...
 *
 * <noncompliant>
 * // add the non-compliant code example here
 * </noncompliant>
 *
 * <compliant>
 * // add the compliant code example here
 * </compliant>
 */
class SomeRule(config: Config = Config.empty) : Rule(config) {

}
```

When authoring the documentation string for a rule, adhere to the following
guidelines:
1. The documentation string shall be formulated in such a manner that it refers
   to the rule itself:
   - :heavy_check_mark:: `Detects trailing spaces and recommends to remove them.`
   - :x:: ``Trailing spaces detected. Consider removing them.``
2. Stick to the [KDoc convention][3] that the first paragraph of documentation
   text is the summary of the documented element (the rule), while the following
   text is a detailed description. More specifically, populate these two regions
   as follows:
   - In the **summary**, give a concise description of the violation(s) that the
   rule identifies. If applicable, a very brief summary of why the violation is
   considered bad practice or what is usually done to eliminate it can be
   given (but does not have to be given).
   - In the **detailed description**, which does not have to be added for
   simple/obvious rules, explain the violation(s) that the rule identifies in
   greater detail. Add a rationale (ideally with a reference) outlining why such
   a violation should be avoided. If applicable, add further remarks that make
   it easier to understand or configure the rule.
3. Formulate all descriptions using (potentially incomplete) sentences with
   proper capitalization and punctuation:
   - :heavy_check_mark:: `Ensures that files with a single non-private class are named accordingly.`
   - :x:: `ensures that files with a single non-private class are named accordingly`
4. Surround inline code (or code symbols) with `` ` `` characters (as in
   [Markdown][4]):
   - :heavy_check_mark:: ``Reports referential equality checks for types such as `String` and `List`.``
   - :x:: `Reports referential equality checks for types such as String and List.`

#### General remarks on issue descriptions and code smell messages

Adhere to the following guidelines when authoring an issue description or code
smell messages:
1. Use a wording that focuses on the violation rather than on the rule itself.
   Therefore, assume that the analyzed codebase contains at least one violation
   of the respective rule.
   - :heavy_check_mark:: ``Duplicated case statements in `when` expression.``
   - :x:: `Detects trailing spaces.`
2. Formulate the text as a sequence of (potentially incomplete) sentences with
   proper capitalization and punctuation:
   - :heavy_check_mark:: ``Duplicated case statements in `when` expression.``
   - :x:: ``duplicated case statements in `when` expression``
3. Separate sentences from the sequence with one space. Most importantly, do not
   use line breaks:
   - :heavy_check_mark:: `Non-boolean property with prefix suggesting boolean type. This might mislead clients of the API.`
   - :x:: `Non-boolean property with prefix suggesting boolean type.\nThis might mislead clients of the API.`
4. Surround inline code (or code symbols) with `` ` `` characters (as in
   [Markdown][4]):
   - :heavy_check_mark:: `` `map.get()` used with non-null assertion operator `!!`.``
   - :x:: `map.get() used with non-null assertion operator (!!).`

*Note*: `non-null` is a borderline case. While `null` itself can be seen as
code, the prefix turns it into a conventional word.

The above-mentioned guidelines tell you *how* to formulate issue descriptions
and code smell messages. To learn *what to include* in each of the texts,
refer to the following two sections.

#### Components of issue descriptions

An issue description should consist of the following components:
1. **Violation** (*required*): What type of violation is it that detekt
   identified in the codebase and, under consideration of the given detekt
   configuration, recognizes as a violation of the considered rule?
2. **Rationale** (*optional*): Why is the identified violation considered an
   issue? Especially for less experienced Kotlin developers, this might not be
   clear from just the violation itself.
3. **Recommendation** (*optional*): What are developers usually expected to do
   in order to eliminate the identified violation? In some cases, this might be
   entirely obvious. If it is not, however, it makes sense to add a suitable
   recommendation (or multiple alternative recommendations).

:warning: If possible, messages should first describe the violation, then the
optional rationale, and finally the optional recommendation.

*Exception*: If it makes sense in the specific case, it is also possible to deviate
from this order. Especially if the analyzed code contains a discouraged design
pattern or, alternatively, can be improved by adopting an encouraged design pattern,
the actual violation would often have to be described as 'making use of the
discouraged pattern' or as 'not making use of an encouraged design pattern'. In this
case, it is possible to focus on the rationale or the recommendation, while the
underlying violation is implicitly mentioned.

:warning: In any case, try to keep the description as brief and concise as
possible!

The following list gives examples of compliant issue descriptions:
- :heavy_check_mark:: `Public library class.` &rarr; Just a violation. Compliant, but would certainly benefit from a few more details.
- :heavy_check_mark:: ``Public library class. Reduce its visibility to the module or the file.`` &rarr; Violation and recommendation.
- :heavy_check_mark:: `Function returns a constant. This is misleading.` &rarr; Violation and rationale.
- :heavy_check_mark:: `Function returns a constant, which is misleading. Use a constant property instead.` &rarr; All components.

The following issue descriptions do not comply with this style guide:
- :x:: `Library class should not be public.` &rarr; Recommendation and violation (given implicitly).
- :x:: `A function that only returns a constant is misleading.` &rarr; Rationale and violation (given implicitly).

Although the violation is implicitly described as part of the recommendation and
the rationale, respectively, these messages do not benefit enough from the
deviation (in comparison to the messages in the compliant examples above).

In contrast, the following message does actually benefit from the implicit
description of the violation:
- :x:: ``The `next()` method of an `Iterator` implementation does not throw a `NoSuchElementException` when there are no more elements to return. In such situations, this Exception should be thrown.`` &rarr; Unnecessarily verbose.
- :heavy_check_mark:: ``The `next()` method of an `Iterator` implementation should throw a `NoSuchElementException` when there are no more elements to return.``
  &rarr; Recommendation and violation (given implicitly).

#### Components of code smell messages

A code smell message should be dynamically created to describe a
*specific violation*. It should be regarded as an extension of the more generic
violation part of the issue description. More specifically, it should explicitly
reference identifiers or similar from the codebase. If it makes
sense in the specific context, it might be worthwhile to add a more detailed
version of the recommendation as well. If this is the case, add this extension
after the more specific description of the violation.

:warning: Try to keep code smell messages even shorter than issue descriptions!

The following list contains compliant and non-compliant examples of code smell
messages:
- :x:: ``Non-boolean property suggests a boolean type.``
  &rarr; Probably not more specific than the issue description.
- :heavy_check_mark:: ``Non-boolean property `hasXyz` suggests a boolean type.``
  &rarr; With a more specific description of the violation.
- :x:: ``Non-boolean property `hasXyz` suggests a boolean type. Remove the prefix.``
  &rarr; Pointless recommendation.
- :heavy_check_mark:: ``Non-boolean property `hasXyz` suggests a boolean type. Remove the `has` prefix.``
  &rarr; Specific recommendation.
- :heavy_check_mark:: ``Magic number `4` passed to `abc`. Pass it as a named argument.``
  &rarr; Usage-aware recommendation.

*Note*: The recommendation given in the last example is usage-aware since
it is limited to cases in which a Kotlin function is called. Named
arguments [cannot be used when calling Java functions][5]. Since this
specific recommendation cannot be given for all encountered magic numbers,
it is not possible to incorporate it into the generic issue description.

## Contributing to the website

Check the [README.md inside website/](https://github.com/detekt/detekt/blob/main/website/README.md).

## Working on the Gradle plugin

- Make changes to the core modules (e.g. adding a new CLI flag)
- Run `gradle publishToMavenLocal`
- Make changes to the Gradle plugin and add tests
- Verify with `gradle detekt`

## Releasing new detekt versions

- `./scripts/github-milestone-report.main.kts` - creates changelog
- `gradle increment<Patch|Minor|Major>` - update version
- `./scripts/release.sh` - publish all artifacts

## Gradle Enterprise Access

We do have access to a managed [Gradle Enterprise instance][6] that is publishing
build scans for all the builds executed on CI (not from forks).

This is extremely helpful to debug build failures and have access to remote build cache.
Build scans are public so everyone can get insights on our build status.

If you're a **maintainer** of a project under github.com/detekt/, you can request an access token
to connect your local machine to the Gradle Enterprise instance, so you will also be publishing scans.

You must follow the steps below:

1. Email us at [info@detekt.dev][7] or get in touch with one of the existing maintainers.
2. An account on https://ge.detekt.dev/ will be created for you, which you need to configure upon login (e.g. reset your password).
3. Run the `./gradlew provisionGradleEnterpriseAccessKey` task from the detekt root folder
4. Complete the access key provisioning process (you will have to go through a browser).
5. Verify that the access key is correctly stored inside `~/.gradle/enterprise/keys.properties`
6. Do a test run (say with `./gradlew tasks`) to verify that a scan is correctly published.

More information on this process could be found on the [official Gradle Enterprise documentation][8].

[1]: https://github.com/detekt/detekt/blob/v1.19.0/detekt-api/src/main/kotlin/io/gitlab/arturbosch/detekt/api/Issue.kt
[2]: https://github.com/detekt/detekt/blob/v1.19.0/detekt-api/src/main/kotlin/io/gitlab/arturbosch/detekt/api/CodeSmell.kt
[3]: https://kotlinlang.org/docs/kotlin-doc.html
[4]: https://daringfireball.net/projects/markdown/syntax
[5]: https://kotlinlang.org/docs/functions.html#named-arguments
[6]: https://ge.detekt.dev/
[7]: mailto:info@detekt.dev
[8]: https://docs.gradle.com/enterprise/gradle-plugin/#automated_access_key_provisioning
