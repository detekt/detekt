# Contributing to detekt

- Read [this article](https://chris.beams.io/posts/git-commit/) before writing commit messages
- Use `gradle build -x dokkaJekyll` to build the source but exclude documentation jar generating to save time.
- `gradle detekt` should not report any errors
- This repository follows the [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html) which are enforced by ktlint when running `gradle detekt`.
- Make sure your IDE uses [ktlint](https://github.com/pinterest/ktlint) formatting rules as well as the settings in [.editorconfig](../.editorconfig)
- We use [Spek](https://github.com/spekframework/spek) for testing. Please use the `Spec.kt`-Suffix. For easier testing you might want to use the [Spek IntelliJ Plugin](https://plugins.jetbrains.com/plugin/10915-spek-framework).
- Feel free to add your name to the contributors list at the end of the readme file when opening a pull request.
- The code in `detekt-api` and any rule in `detekt-rules` must be documented. We generate documentation for our website based on these modules.
- If some Kotlin code in `resources` folder (like `detekt-formatting`) shows a compilation error, right click on it and use `Mark as plain text`.

### When implementing new rules ...

- ... do not forget to add the new rule to a `RuleSetProvider` (e.g. StyleGuideProvider)
- ... do not forget to write a description for the issue of the new rule.
- ... add the [correct KDoc](#contents-and-structure-of-a-rules-kdoc) and [annotations](#rule-annotations) to your `Rule` class. This is used to generate documentation pages and the `default-detekt-config.yml` automatically.
- ... do not forget to test the new rule and/or add tests for any changes made to a rule.
Run detekt on itself and other kotlin projects with the `--run-rule RuleSet:RuleId` option to test your rule in isolation.
Make use of the `scripts/get_analysis_projects.groovy` script to automatically establish a set of analysis projects.
- ... run `./gradlew generateDocumentation` to add your rule and its config options to the `default-detekt-config.yml`.
- ... do not forget to run `./gradlew build`. This will execute tests locally.
- To print the AST of sources you can pass the `--print-ast` flag to the CLI which will print each
Kotlin files AST. This can be helpful when implementing and debugging rules.
- To view the AST (PSI) of your source code you can use the [PSI Viewer plugin](https://plugins.jetbrains.com/plugin/227-psiviewer) for IntelliJ.
- be aware that your PR will stay open for at least two days so that other users can give feedback.

After some time and testing there is a chance this rule will become active on default.

#### Contents and structure of a rule's KDoc

```kotlin
/**
  * This is a nice description for the rule explaining what it checks, why it
  * exists and how violations can be solved.
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

The description should be as detailed as possible as it will act as the documentation of the rule. Add links to 
references that explain the rationale for the rule if possible.

The `<noncompliant>` and `<compliant>` code examples should be added right after the description of the rule.

#### Rule annotations

```kotlin
@ActiveByDefault(since = "1.0.0")
@RequiresTypeResolution
class SomeRule(config: Config = Config.empty) : Rule(config) {

    @Configuration("This is the description for the configuration parameter below.")
    private val name: String by config(default = "whatever should be the default")

}
```

Use the `@Configuration` annotation in combination with the `config` delegate to create a configurable property for your rule. The name of the property will become the key and the provided default will be the value in the `default-detekt-config.yml`. All information are also used to generate the rule documentation in the wiki. 
Note that a property that is marked with `@Configuration` must use the config delegate (and vice versa).

Rules annotated with `@ActiveByDefault` will be marked as active in the `default-detekt-config.yml`. Generally this will not be the case for new rules.

A rule that requires type resolution must be marked with `@RequiresTypeResolution`. See [the type resolution wiki page](../docs/pages/gettingstarted/type-resolution.md) for more detail on this topic.

The rule defined above will translate to a rule entry in the `default-detekt-config.yml`:
```yml
SomeRule:
    active: true
    name: 'whatever should be the default'
```


### When updating the website ...

Make sure to test your changes locally.

- install ruby and jekyll
- gem install bundler
- bundler install
- jekyll build
- jekyll serve

Following warning is expected until [Jekyll](https://github.com/jekyll/jekyll/issues/7947) adopts to Ruby 2.7.0.

`warning: Using the last argument as keyword parameters is deprecated (Ruby 2.7.0)`

### When working on the Gradle plugin ...

- Make changes to the core modules (e.g. adding a new CLI flag)
- Run `gradle publishToMavenLocal`
- Make changes to the Gradle plugin and add tests
- Verify with `gradle detekt`

### Release process

- `./scripts/github-milestone-report.main.kts` - creates changelog
- `gradle increment<Patch|Minor|Major>` - update version
- `./scripts/release.sh` - publish all artifacts
