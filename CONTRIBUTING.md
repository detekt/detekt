# Contributing to detekt

- Read [this article](https://chris.beams.io/posts/git-commit/) before writing commit messages
- `detekt` itself should not report any new errors
- This repo uses tabs!
- Use idea-code-style.xml for coding style 
- Run `detektFormat` before commiting
- Make sure `when {` or `object : MyType { ... ` is on the same line as `=` eg. 
```kotlin
    fun stuff(x: Int) = when(x) {
        1..10 -> ...
        else -> ...
    }
```

### When implementing new rules ...

- ... do not forget to add the new rule to a `RuleSetProvider`
- ... do not forget to add the new rule to the `default-detekt-config.yml` in the `detekt-cli-resources`
- ... do not forget to write a description for the issue of the new rule

### Release checklist

- new features? -> Update README.md / from `1.x.x` Wiki
- add changes in CHANGELOG.md -> `groovy github-milestone-report.groovy arturbosch/detekt [milestone-number]`
- migrations expected? -> Update MIGRATION_GUIDE.md
- all new contributors mentioned? -> README.md>Contributors, Update `all contributors`-Badge
- new gradle-plugin release? -> Update gradle-version badge
