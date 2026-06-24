---
title: Ruleauthors Rule Set
sidebar: home_sidebar
keywords: [rules, ruleauthors]
permalink: ruleauthors.html
toc: true
folder: documentation
---
Rule Set ID: `ruleauthors`

The rule authors ruleset provides rules that ensures good practices when writing detekt rules.

**Note: The `ruleauthors` rule set is not included in the detekt-cli or Gradle plugin.**

To enable this rule set, add `detektPlugins "dev.detekt:detekt-rules-ruleauthors:$version"`
to your Gradle `dependencies` or reference the `detekt-rules-ruleauthors`-jar with the `--plugins` option
in the command line interface.

### LeakingSessionBoundType

Detects when lambdas passed to `analyze {}` return types implementing `KaSession` or `KaLifetimeOwner`.

These objects are lifetime-scoped to the analysis block and will cause runtime errors if they
escape. Return a plain data class, primitive, or `KaSymbolPointer` instead.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
val type = analyze(element) {
    element.expressionType // returns KaType which implements KaLifetimeOwner
}
```

#### Compliant Code:

```kotlin
val typeName = analyze(element) {
    element.expressionType?.toString() // returns a String instead
}
```

### UseEntityAtName

If a rule [report]s issues using [Entity.from] with [KtNamedDeclaration.getNameIdentifier],
then it can be replaced with [Entity.atName] for more semantic code and better baseline support.

**Active by default**: Yes - Since v1.22.0
