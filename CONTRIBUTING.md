# Contributing to detekt

- Read [this article](https://chris.beams.io/posts/git-commit/) before writing commit messages
- `gradle detektCheck` should not report any errors
- This repo uses tabs! Make sure your code is properly formatted.
- Use idea-code-style.xml for coding style .
- We use [Spek](https://github.com/spekframework/spek) for testing.

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
- ... do not forget to add the new rule to the `default-detekt-config.yml` in the `detekt-cli-resources`.
- ... do not forget to write a description for the issue of the new rule.
- be aware that your PR will stay open for at least two days so that other users can give feedback.
- make sure to set the new rule `active: false` in the default config. Brave users can test it on demand. 
After some time and testing there is a chance this rule will become active on default.

### Release checklist

- new features? -> Update README.md / from `1.x.x` Wiki
- add changes in CHANGELOG.md -> `groovy github-milestone-report.groovy arturbosch/detekt [milestone-number]`
- migrations expected? -> Update MIGRATION_GUIDE.md
- all new contributors mentioned? -> README.md>Contributors, Update `all contributors`-Badge
- new gradle-plugin release? -> Update gradle-version badge
