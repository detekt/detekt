---
title: Libraries Rule Set
sidebar: home_sidebar
keywords: [rules, libraries]
permalink: libraries.html
toc: true
folder: documentation
---
Rules in this rule set report issues related to libraries API exposure.

**Note: The `libraries` rule set is not included in the detekt-cli or Gradle plugin.**

To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-rules-libraries:$version"`
to your Gradle `dependencies` or reference the `detekt-rules-libraries`-jar with the `--plugins` option
in the command line interface.

### ForbiddenPublicDataClass

Data classes are bad for binary compatibility in public APIs. Avoid using them.

This rule is aimed at library maintainers. If you are developing a final application you can ignore this issue.

More info: [Public API challenges in Kotlin](https://jakewharton.com/public-api-challenges-in-kotlin/)

**Active by default**: Yes - Since v1.16.0

#### Configuration options:

* ``ignorePackages`` (default: ``['*.internal', '*.internal.*']``)

  ignores classes in the specified packages.

#### Noncompliant Code:

```kotlin
data class C(val a: String) // violation: public data class
```

#### Compliant Code:

```kotlin
internal data class C(val a: String)
```

### LibraryCodeMustSpecifyReturnType

Functions/properties exposed as public APIs of a library should have an explicit return type.
Inferred return type can easily be changed by mistake which may lead to breaking changes.

See also: [Kotlin 1.4 Explicit API](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

#### Configuration options:

* ``allowOmitUnit`` (default: ``false``)

  if functions with `Unit` return type should be allowed without return type declaration

#### Noncompliant Code:

```kotlin
// code from a library
val strs = listOf("foo, bar")
fun bar() = 5
class Parser {
    fun parse() = ...
}
```

#### Compliant Code:

```kotlin
// code from a library
val strs: List<String> = listOf("foo, bar")
fun bar(): Int = 5

class Parser {
    fun parse(): ParsingResult = ...
}
```

### LibraryEntitiesShouldNotBePublic

Library typealias and classes should be internal or private.

**Active by default**: Yes - Since v1.16.0

#### Noncompliant Code:

```kotlin
// code from a library
class A
```

#### Compliant Code:

```kotlin
// code from a library
internal class A
```
