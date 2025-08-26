---
id: compose
title: "Configuration for Compose"
keywords: [compose, config, configuration, jetpack-compose, rules]
summary: This page describes each reporting format and explains how to leverage them.
sidebar_position: 5
---

Relevant rule sets and their configuration options for Compose styles & usage. The following are being used as reference for Compose usage:
- [Compose API Guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md)
- [Compose source](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose)

### FunctionNaming

See [FunctionNaming](../rules/naming#functionnaming).

`@Composable` functions that return `Unit` are named using `PascalCase`. Detekt may see this as a violation:

```kotlin
@Composable
fun FooButton(text: String, onClick: () -> Unit) { // Violation for FooButton()
```

#### Configurations:
Choose _either_ of the following options:

* Augment default `functionPattern` to `'[a-zA-Z][a-zA-Z0-9]*'` (default is: `'[a-z][a-zA-Z0-9]*'`)
* Set `ignoreAnnotated` to `['Composable']`

### TopLevelPropertyNaming

See [TopLevelPropertyNaming](../rules/naming#toplevelpropertynaming).

Compose guidelines prescribe `CamelCase` for top-level constants.

##### Default Style:

```kotlin
private val FOO_PADDING = 16.dp
```

##### Compose Style:

```kotlin
private val FooPadding = 16.dp
```

#### Configurations:

* Set `constantPattern` to `'[A-Z][A-Za-z0-9]*'` (default is: `'[A-Z][_A-Z0-9]*'`)


### LongParameterList

See [LongParameterList](../rules/complexity#longparameterlist).

Composables may boast more than the typical number of function arguments (albeit mostly with default values). For example, see [OutlinedTextField](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/OutlinedTextField.kt;l=133?q=OutlinedTextFieldLayout&ss=androidx%2Fplatform%2Fframeworks%2Fsupport:compose%2F).

#### Configurations:

* Set `functionThreshold` to a higher value
* Additionally, can set `ignoreDefaultParameters = true`

### MagicNumber

See [MagicNumber](../rules/style#magicnumber).

Class/companion object/top-level properties that declare objects such as `Color(0xFFEA6D7E)` may be considered violations if they don't specify the named parameter (i.e. `Color(color = 0xFFEA6D7E)`).

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

* Set `ignorePropertyDeclaration = true`, `ignoreCompanionObjectPropertyDeclaration = true` (default)

### UnusedPrivateMember

See [UnusedPrivateMember](../rules/style#unusedprivatemember).

Detekt may see composable preview functions, i.e. those marked with `@Preview`, as unused.

``` kotlin
@Preview
@Composable
private fun FooLazyColumnPreview() { // Violation for FooLazyColumnPreview()
    FooLazyColumn()
}
```

#### Configurations:

* Set `ignoreAnnotated` to `['Preview']`
