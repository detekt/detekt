---
title: Customizing for Compose
sidebar: home_sidebar
keywords: rules, compose, jetpack-compose
permalink: compose.html
toc: true
folder: documentation
---

Relevant rule sets and their configuration options for Compose styles & usage.

### TopLevelPropertyNaming

See [TopLevelPropertyNaming](https://detekt.github.io/detekt/naming.html#toplevelpropertynaming).

Compose [guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md#singletons-constants-sealed-class-and-enum-class-values) prescribe `CamelCase` for top-level constants.

##### Default Style:

```kotlin
private val FOO_PADDING = 16.dp
```

##### Compose Style:

```kotlin
private val FooPadding = 16.dp
```

#### Configurations:

* Set ``constantPattern`` to ``'[A-Z][_A-Za-z0-9]*'`` (default: ``'[A-Z][_A-Z0-9]*'``)


### LongParameterList

See [LongParameterList](https://detekt.github.io/detekt/complexity.html#longparameterlist).

Composables may boast more than the typical number of function arguments (albeit mostly with default values). For example, see [OutlinedTextField](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/OutlinedTextField.kt;l=133?q=OutlinedTextFieldLayout&ss=androidx%2Fplatform%2Fframeworks%2Fsupport:compose%2F).

#### Configurations:

* Set ``functionThreshold`` to a higher value
* Additionally, can set ``ignoreDefaultParameters = true``

### MagicNumber

See [MagicNumber](https://detekt.github.io/detekt/style.html#magicnumber).

Class/companion object/top-level properties that declare objects such as ``Color(0xFFEA6D7E)`` may be considered violations if they don't specify the named parameter (i.e. ``Color(color = 0xFFEA6D7E)``).

``` kotlin
val color1 = Color(0xFFEA6D7E) // Violation

class Foo {
  val color2 = Color(0xFFEA6D7E) // Violation

  companion object {
    val color3 = Color(0xFFEA6D7E) // No violation if ignoreCompanionObjectPropertyDeclaration = true by default
  }
}
```

#### Configurations:

* Set ``ignorePropertyDeclaration = true``, ``ignoreCompanionObjectPropertyDeclaration = true`` (default)