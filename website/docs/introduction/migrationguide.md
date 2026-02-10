---
id: migration
title: "Migration guide to 2.0.0"
keywords: [migration]
sidebar_position: 80
---

This is a **work-in-progress** migration guide from detekt 1.x to detekt 2.x. If you find issues with your migration while
following this guide, please open an issue or, even better, if you find the solution yourself, update this guide so the
next person doesn't find the same problem.

On this page, you will find two different guides: one for detekt users and another for 3rd-party detekt rule maintainers
(or any other detekt extension).

## User guide

### gradle plugin id

The gradle plugin id was changed from `io.gitlab.arturbosch.detekt` to `dev.detekt`.

So, where you had something like this:

```kotlin
id("io.gitlab.arturbosch.detekt") version "1.23.8"
```

You need to change it to:

```kotlin
id("dev.detekt") version "[detekt_version]"
```

### Module rename

The formatting module was renamed from `detekt-formatting` to `detekt-rules-ktlint-wrapper`.

## Rule author guide

### Dependency coordinates and imports

detekt 2.0 changed its coordinates and packages, so you need to fix those. This should be easy: replace all the
occurrences of `io.gitlab.arturbosch.detekt` with `dev.detekt`.

### Rule

The API of `Rule` changed.

1. The constructor of `Rule` now asks for the rule description. This is the description that you had in the `issue`
parameter. You can just move it from issue to the second constructor parameter.
2. The property `issue` is no longer needed, so you can remove it.
   - Note that the concept of `Debt` doesn't exist anymore.
3. `CodeSmell` was renamed to `Finding`. The API is nearly the same, but you don't need to pass an `issue`.

### Analysis API - the old type solving

If your rules used type solving, you should port your rule from using `ContextBinding` to using the `analyze(KtElement) {}` API.

The annotation `@RequiresTypeSolving` is replaced with the interface `RequiresAnalysisApi`. Your rule should implement
this interface to use the Analysis API.

JetBrains has its own migration guide: https://kotlin.github.io/analysis-api/migrating-from-k1.html

It's difficult to create a guide for this migration. The good part is that at detekt we have a lot of PRs doing exactly
this porting, so you can probably find examples that will be nearly the same as what you need:

- [Complexity](https://github.com/detekt/detekt/issues/8039)
- [Coroutines](https://github.com/detekt/detekt/issues/8040)
- [ErrorProne](https://github.com/detekt/detekt/issues/8041)
- [Exceptions](https://github.com/detekt/detekt/issues/8042)
- [Libraries](https://github.com/detekt/detekt/issues/8043)
- [Naming](https://github.com/detekt/detekt/issues/8044)
- [Performance](https://github.com/detekt/detekt/issues/8045)
- [Style](https://github.com/detekt/detekt/issues/8046)

#### Testing rules

We also changed our API for testing rules a bit.

1. If you were using our custom assertions from AssertJ, you should now depend on `implementation("dev.detekt:detekt-test-assertj:[detekt_version]")`.
2. The function `compileAndLint` was renamed to `lint` and `compileAndLintWithContext` was renamed to `lintWithContext`.

### RuleSetProvider

On detekt 1.x, a `RuleSetProvider` was something similar to this:
```kotlin
class MyRuleSetProvider : RuleSetProvider {
    override val ruleSetId = "MyRuleSet"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            MyRule(config),
        ),
    )
}
```

And it should look something like this for 2.x:
```kotlin
class MyRuleSetProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("MyRuleSet")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::MyRule,
        )
    )
}
```
