# Contributing to detekt

- Read [this article](https://chris.beams.io/posts/git-commit/) before writing commit messages
- Copy the file `commit-msg` to `.git/hooks`
- `gradle detektCheck` should not report any errors
- This repo uses tabs! Make sure your code is properly formatted.
- Use idea-code-style.xml for coding style .
- We use [Spek](https://github.com/spekframework/spek) for testing.
- Feel free to add your name to the contributors list at the end of the readme file when opening a pull request.

### Specific code style rules we use

- If you modify a file (any changes) or add a new file authored by yourself, add a `@author Your_Name` tag. 
If it is your first contribution, feel free to add yourself to the list of contributors at the end of the readme file.
- There must be a newline between the start of the class body and the first member declaration:
```kotlin
class A { // wrong!
    val a = 5
}
  
class B { // right!
  
    val b = 5
}
```
- Make sure `when {` or `object : MyType { ... ` is on the same line as `=` eg. 
```kotlin
    fun stuff(x: Int) = when(x) {
        1..10 -> ...
        else -> ...
    }
```

### When implementing new rules ...

- ... do not forget to add the new rule to a `RuleSetProvider`.
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
     * @configuration name - Description for the configuration option (default: "whatever should be the default")
     *
     * @author name
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

    Last but not least, the `@author` tag should be added.

- ... do not forget to test the new rule and/or add tests for any changes made to a rule. 
- ... do not forget to run `./gradlew build`. This will execute tests locally and update the `default-detekt.config.yml`
as well as add the new/changed rules to the documentation.
- be aware that your PR will stay open for at least two days so that other users can give feedback.

After some time and testing there is a chance this rule will become active on default.

Rules that contain an `@active` tag in their KDoc will be marked as active in the `default-detekt-config.yml`.

### Release checklist

- new features? -> Update README.md / from `1.x.x` Wiki
- add changes in CHANGELOG.md -> `groovy github-milestone-report.groovy arturbosch/detekt [milestone-number]`
- migrations expected? -> Add migration sub-section to current release changelog in CHANGELOG.md
- all new contributors mentioned? -> README.md>Contributors, Update `all contributors`-Badge
- new gradle-plugin release? -> Update gradle-version badge
