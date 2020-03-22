# Contributing to detekt

- Read [this article](https://chris.beams.io/posts/git-commit/) before writing commit messages
- Use `gradle build -x dokka` to build the source but exclude documentation jar generating to save time.
- `gradle detekt` should not report any errors
- This repository follows the [Kotlin Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html) which are enforced by KtLint when running `gradle detekt`.
- Make sure your IDE uses [KtLint](https://github.com/shyiko/ktlint) formatting rules as well as the settings in [.editorconfig](../.editorconfig)
- We use [Spek](https://github.com/spekframework/spek) for testing. Please use the `Spec.kt`-Suffix. For easier testing you might want to use the [Spek IntelliJ Plugin](https://plugins.jetbrains.com/plugin/10915-spek-framework).
- Feel free to add your name to the contributors list at the end of the readme file when opening a pull request.
- The code in `detekt-api` and any rule in `detekt-rules` must be documented. We generate documentation for our website based on these modules.
- If some Kotlin code in `resources` folder (like `detekt-formatting`) shows a compilation error, right click on it and use `Mark as plain text`.

### When implementing new rules ...

- ... do not forget to add the new rule to a `RuleSetProvider` (e.g. StyleGuideProvider)
- ... do not forget to write a description for the issue of the new rule.
- Add the correct KDoc to the Rule class. This KDoc is used to generate wiki pages and the `default-detekt-config.yml`
automatically. The format of the KDoc should be as follows:

    ```kotlin
    /**
     * This is a nice description for the rule, explaining what it checks, why it exists and how violations can be
     * solved.
     *
     * <noncompliant>
     * // add the non-compliant code example here
     * </noncompliant>
     *
     * <compliant>
     * // add the compliant code example here
     * </compliant>
     *
     * @configuration name - Description for the configuration option (default: `whatever should be the default`)
     */
    class SomeRule : Rule {
  
    }
    ```
    
    The description should be as detailed as possible as it will act as the documentation of the rule. Add links to 
    references that explain the rationale for the rule if possible.
    The `<noncompliant>` and `<compliant>` code examples should be added right after the description of the rule.
    The `@configuration` tag should follow the correct pattern. The name of the configuration option *has* to match the 
    actual name used in the code, otherwise an invalid `default-detekt-config.yml` will be generated and the rule won't
    function correctly by default. 
    The default value will be taken as is for the configuration option and pasted into the `default-detekt-config.yml`.
    
    A `@configuration` tag as described above will translate to a rule entry in the `default-detekt-config.yml`:
    ```yml
    SomeRule:
       active: false
       name: whatever should be the default
    ```

- ... do not forget to test the new rule and/or add tests for any changes made to a rule.
Run detekt on itself and other kotlin projects with the `--run-rule RuleSet:RuleId` option to test your rule in isolation.
Make use of the `scripts/get_analysis_projects.groovy` script to automatically establish a set of analysis projects.
- ... do not forget to run `./gradlew build`. This will execute tests locally and update the `default-detekt.config.yml`
as well as add the new/changed rules to the documentation.
- To print the AST of sources you can pass the `--print-ast` flag to the CLI which will print each
Kotlin files AST. This can be helpful when implementing and debugging rules.
- To view the AST (PSI) of your source code you can use the [PSI Viewer plugin](https://plugins.jetbrains.com/plugin/227-psiviewer) for IntelliJ.
- be aware that your PR will stay open for at least two days so that other users can give feedback.

After some time and testing there is a chance this rule will become active on default.

Rules that contain an `@active` tag in their KDoc will be marked as active in the `default-detekt-config.yml`.

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

### Release checklist

- add changes in CHANGELOG.md -> `groovy github-milestone-report.groovy arturbosch detekt [milestone-number]`
- migrations expected? -> Add `Migration` sub-section
- important non-breaking changes? Add `Notable Changes` sub-section
- all new contributors mentioned? -> README.md>Contributors, Update `all contributors`-Badge

#### Releasing process

- Increment `detektVersion` in `gradle.properties`
- `gradle build publishToMavenLocal`
- Increment version of the Gradle Plugin
- `gradle bU` - uploads artifacts to Bintray
- `gradle pluPub` - uploads the Gradle Plugin to the Plugin Repositories
