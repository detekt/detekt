---
id: suppressors
title: "Suppressors"
keywords: [suppressing, issues, smells]
sidebar_position: 8
---

The `Suppressor`s are a tool that you can use to customize the reports of detekt. They allow you to (surprise) suppress some issues detected by some rules, and they can be applied to any rule.

An example is the **annotation** suppressor. It works like this. First, you need to configure the tag `ignoreAnnotated` with a list of annotations, you want the suppressor to consider. Example:

```yaml
UnusedPrivateMember:
  active: true
  ignoreAnnotated:
    - 'Preview'
```

Now, if an issue is found under a code that is annotated with `@Preview` that issue will be suppressed. This example is really handy if you use [Jetpack Compose](../introduction/compose), for example.

## Available `Suppressor`s

### Annotation Suppressor

Suppress all the issues that are raised under a code that is annotated with the annotations defined at `ignoreAnnotated`.

##### Config tag

`ignoreAnnotated: List<String>`: The annotations can be defined just by its name or with its fully qualified name. If you don't run detekt with type solving the fully qualified name does not work.

### Function Suppressor

Suppress any issue raised under a function definition that matches the signatures defined at `ignoreFunction`.

*Note*: this Suppressor doesn't suppress issues found when you call these functions. It just suppresses the ones in the function **definition**.

##### Config tag:

`ignoreFunction: List<String>`: The signature of the function. You can ignore all the overloads of a function defining just its name like `java.time.LocalDate.now` or you can specify the parameters to only suppress one: `java.time.LocalDate(java.time.Clock)`.

*Note:* you need to write all the types with fully qualified names e.g. `org.example.foo(kotlin.String)`. It is important to add `kotlin.String`. Just adding `String` will not work.
