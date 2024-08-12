---
title: "Using Type Resolution"
keywords: [detekt, static, analysis, code, kotlin]
sidebar: 
permalink: type-resolution.html
folder: gettingstarted
summary:
sidebar_position: 5
---

This page describes how to use detekt's **type resolution** feature.

:::caution

Please note that type resolution is still an **experimental feature** of detekt. We expect it to be stable with the upcoming release of detekt (2.x)

:::

## What is type resolution

Type resolution is a feature that allows detekt to perform more **advanced** static analysis on your Kotlin source code. 

Normally, detekt doesn't have access to the types and symbols that are available to the compiler during the compilation. This restricts the inspection capability.
By enabling type resolution, you provide to detekt all the information to understand types and symbols in your code needed to perform more accurate analysis. This extends detekt's inspection capability to ones of the Kotlin **compiler**.

### An example

detekt has a rule called [MagicNumber](/docs/rules/style#magicnumber) to detect usages of magic numbers in your code. 

In the following code:

```kotlin
val user = getUserById(42)?.toString()
```

detekt is able to report the usage of the number `42` as a magic number, **without** type resolution. All the information needed to run this inspection is already available in the source code.

Similarly, detekt has another rule called [UnnecessarySafeCall](/docs/rules/potential-bugs#unnecessarysafecall) to detect unnecessary usages of safe call operators (`?.`).

In the previous example, detekt is able to determine if the safe call in `getUserById(42)?.toString()` is required **only with** type resolution. 

This is because detekt needs to know what is the **return type** of `getUserById()` in order to correctly perform the inspection. If the return type is a nullable type, then the code is valid. If the return type is a non-nullable type, detekt will report an `UnnecessarySafeCall` as the `?.` is actually not needed.

With type resolution, detekt has access to all the symbols and types of your codebase. Type resolution can be enabled by providing the **classpath** that is used during compilation. This will give detekt access to all the code used to compile your project (both first and third party code) and will allow more advanced analysis.

## Is my rule using type resolution?

If you're running detekt **without** type resolution, all the rules that require type resolution **will not run**.

All the rules that require type resolution are annotated with [`@RequiresTypeResolution`](https://github.com/detekt/detekt/search?q=%40RequiresTypeResolution). 

Moreover, their official documentation in the detekt website will mention _Requires Type Resolution_ ([like here](/docs/rules/potential-bugs#unnecessarysafecall)).

:::caution

Please note that we do have some rules that have mixed behavior whether type resolution is enabled or not. Those rules are listed here: [#2994](https://github.com/detekt/detekt/issues/2994)

:::

Before opening an issue that you're rule is not working, please verify, whether your rule requires type resolution and check if you have type resolution enabled.

Issues and proposals for rules that require type resolution are labelled with [needs type and symbol solving](https://github.com/detekt/detekt/labels/needs%20type%20and%20symbol%20solving) on the Issue tracker.

## Enabling on a JVM project

The easiest way to use type resolution is to use the Detekt Gradle Plugin. On a JVM project, the following tasks will be created:

- `detekt` - Runs detekt WITHOUT type resolution
- `detektMain` - Runs detekt with type resolution on the `main` source set
- `detektTest` - Runs detekt with type resolution on the `test` source set

Moreover, you can use `detektBaselineMain` and `detektBaselineTest` to create baselines starting from runs of detekt with type resolution enabled.

Alternatively, you can create a **custom detekt task**, making sure to specify the `classpath` and `jvmTarget` properties correctly. See the [Run detekt using the Detekt Gradle Plugin](/docs/gettingstarted/gradle) and the [Run detekt using Gradle Task](/docs/gettingstarted/gradletask) for further readings on this.

## Enabling on an Android project

Other than the aforementioned tasks for JVM projects, you can use the following Android-specific gradle tasks:

- `detekt<Variant>` - Runs detekt with type resolution on the specific build variant
- `detektBaseline<Variant>` - Creates a detekt baselines starting from a run of detekt with type resolution enabled on the specific build variant.

Alternatively, you can create a **custom detekt task**, making sure to specify the `classpath` and `jvmTarget` properties correctly.
Doing this on Android is more complicated due to build types/flavors (see [#2259](https://github.com/detekt/detekt/issues/2259) for further context).
Therefore, we recommend using the `detekt<Variant>` tasks offered by the Gradle plugins.

In case of build related issues, you may try `detekt.android.disabled=true` in `gradle.properties` to prevent detekt
Gradle plugins from configuring Android-specific gradle tasks.

## Enabling on a KMP project

The Detekt Gradle Plugin in a Kotlin Multiplatform project automatically generates platform-specific and common tasks for running analysis with type resolution. In general there will be a `detektMetadata<Platform>Main` task for each native platform target configured in the multiplatform plugin, specific tasks for JVM and Android modules as listed below, and a `detektMetadataCommonMain` for the `common` module.

- `detekt` - Runs detekt WITHOUT type resolution
- `detektMetadataCommonMain` - Runs detekt with type resolution on the `common` module in the `main` source set
- `detektJvmMain` - Runs detekt with type resolution on the `jvm` module in the `main` source set
- `detektMetadataIosMain` - Runs detekt with type resolution on the `ios` module in the `main` source set
- `detektAndroid<Variant>` - Runs detekt with type resolution on the `android` module for the specific variant e.g. `detektAndroidRelease`

## Enabling on Detekt CLI

If you're using [detekt via CLI](/docs/gettingstarted/cli), type resolution will be enabled only if you provide the `--classpath` and
`--jvm-target` flags. See the list of [CLI options](/docs/gettingstarted/cli#use-the-cli) for details.

## Writing a rule that uses type resolution

If you're [writing a custom rule](/docs/introduction/extensions) or if you're willing to write a rule to contribute to detekt, you might want to leverage type resolution.

Rules that are using type resolution, access the [bindingContext](https://github.com/JetBrains/kotlin/blob/master/compiler/frontend/src/org/jetbrains/kotlin/resolve/BindingContext.java) from the `BaseRule` class ([source](https://github.com/detekt/detekt/blob/cd659ce8737fb177caf140f46f73a1a86b22be56/detekt-api/src/main/kotlin/io/gitlab/arturbosch/detekt/api/internal/BaseRule.kt#L30)).

By default, the `bindingContext` is initialized as `BindingContext.EMPTY`. This is the **default value** that the rule receives if type resolution is **not enabled**.

Therefore, is generally advised to annotate your `Rule` with `@RequiresTypeResolution` to ensure that your rule doesn't run if you don't have a proper `BindingContext`.

If your rule is annotated with `@RequiresTypeResolution` you are free to use it to resolve types and get access to all the information needed for your rules. As a rule of thumb, we recommend to get inspiration from other rules on how they're using the `bindingContext`.

## Testing a rule that uses type resolution

To test a rule that uses type resolution, you can use the [`lintWithContext`](https://github.com/detekt/detekt/blob/d3546ff0d539d57e7a502dacbf66e91587fff098/detekt-test/src/main/kotlin/io/gitlab/arturbosch/detekt/test/RuleExtensions.kt#L40-L44) and [`compileAndLintWithContext`](https://github.com/detekt/detekt/blob/cd659ce8737fb177caf140f46f73a1a86b22be56/detekt-test/src/main/kotlin/io/gitlab/arturbosch/detekt/test/RuleExtensions.kt#L63-L72) extension functions.

If you're using JUnit 5 for testing, you can use the `@KotlinCoreEnvironmentTest` annotation on your test class, and
accept a parameter of type `KotlinCoreEnvironment` in the class constructor. You can then access the environment by
referencing the parameter specified in the constructor:

```kotlin
@KotlinCoreEnvironmentTest
class MyRuleSpec(private val env: KotlinCoreEnvironment) {
    @Test
    fun `reports cast that cannot succeed`() {
        val code = """/* The code you want to test */"""
        assertThat(MyRuleSpec().compileAndLintWithContext(env, code)).hasSize(1)
    }
}
```

If you're using another testing framework (e.g. JUnit 4), you can use the [`createEnvironment()`](https://github.com/detekt/detekt/blob/cd659ce8737fb177caf140f46f73a1a86b22be56/detekt-test-utils/src/main/kotlin/io/github/detekt/test/utils/KotlinCoreEnvironmentWrapper.kt#L26-L31) method from `detekt-test-utils`.
