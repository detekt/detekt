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

### FunctionNaming for Compose

See [FunctionNaming](/docs/rules/naming#functionnaming).

`@Composable` functions that return `Unit` are named using `PascalCase`. detekt may see this as a violation:

```kotlin
@Composable
fun FooButton(text: String, onClick: () -> Unit) { // Violation for FooButton()
```

#### Recommended configuration
Choose _either_ of the following options:

* Augment default `functionPattern` to `'[a-zA-Z][a-zA-Z0-9]*'` (default is: `'[a-z][a-zA-Z0-9]*'`)
* Set `ignoreAnnotated` to `['Composable']`

### TopLevelPropertyNaming for Compose

See [TopLevelPropertyNaming](/docs/rules/naming#toplevelpropertynaming).

Compose guidelines prescribe `PascalCase` for top-level constants.

##### Default Style:

```kotlin
private val FOO_PADDING = 16.dp
```

##### Compose Style:

```kotlin
private val FooPadding = 16.dp
```

#### Recommended configuration

* Set `constantPattern` to `'[A-Z][A-Za-z0-9]*'` (default is: `'[A-Z][_A-Z0-9]*'`)


### LongParameterList for Compose

See [LongParameterList](/docs/rules/complexity#longparameterlist).

Composables may boast more than the typical number of function arguments (albeit mostly with default values). For example, see [OutlinedTextField](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material/material/src/commonMain/kotlin/androidx/compose/material/OutlinedTextField.kt;l=133?q=OutlinedTextFieldLayout&ss=androidx%2Fplatform%2Fframeworks%2Fsupport:compose%2F).

#### Recommended configuration

* Set `functionThreshold` to a higher value
* Additionally, can set `ignoreDefaultParameters = true`

### MagicNumber for Compose

See [MagicNumber](/docs/rules/style#magicnumber).

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

#### Recommended configuration

* Set `ignorePropertyDeclaration = true`, `ignoreCompanionObjectPropertyDeclaration = true` (default)

### UnusedPrivateMember for Compose

See [UnusedPrivateMember](/docs/rules/style#unusedprivatemember).

detekt may see composable preview functions, i.e. those marked with `@Preview`, as unused.

``` kotlin
@Preview
@Composable
private fun FooLazyColumnPreview() { // Violation for FooLazyColumnPreview()
    FooLazyColumn()
}
```

#### Recommended configuration

* Set `ignoreAnnotated` to `['Preview']`
* 
### TooManyFunctions for Compose

See [TooManyFunctions](/docs/rules/complexity/#toomanyfunctions).

detekt may flag files with many composable preview functions, i.e. those marked with `@Preview`, 
as having too many functions. Since preview functions do not contribute to complexity, this might not be
desired.

#### Recommended configuration

* Set `ignoreAnnotatedFunctions` to `['Preview']`
