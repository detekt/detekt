---
title: Formatting Rule Set
sidebar: home_sidebar
keywords: rules, formatting
permalink: formatting.html
toc: true
folder: documentation
---
This rule set provides rules that address formatting issues.

### ChainWrapping

See https://ktlint.github.io for documentation.

### FinalNewline

See https://ktlint.github.io for documentation.

### ImportOrdering

See https://ktlint.github.io for documentation.

### Indentation

See https://ktlint.github.io for documentation.

### MaxLineLength

This rule reports lines of code which exceed a defined maximum line length.

Long lines might be hard to read on smaller screens or printouts. Additionally having a maximum line length
in the codebase will help make the code more uniform.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* `maxLineLength` (default: `120`)

   maximum line length

* `excludePackageStatements` (default: `false`)

   if package statements should be ignored

* `excludeImportStatements` (default: `false`)

   if import statements should be ignored

* `excludeCommentStatements` (default: `false`)

   if comment statements should be ignored

### ModifierOrder

This rule reports cases in the code where modifiers are not in the correct order. The default modifier order is
taken from: http://kotlinlang.org/docs/reference/coding-conventions.html#modifiers

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
lateinit internal private val str: String
```

#### Compliant Code:

```kotlin
private internal lateinit val str: String
```

### NoBlankLineBeforeRbrace

See https://ktlint.github.io for documentation.

### NoConsecutiveBlankLines

See https://ktlint.github.io for documentation.

### NoEmptyClassBody

See https://ktlint.github.io for documentation.

### NoItParamInMultilineLambda

See https://ktlint.github.io for documentation.

### NoLineBreakAfterElse

See https://ktlint.github.io for documentation.

### NoLineBreakBeforeAssignment

See https://ktlint.github.io for documentation.

### NoMultipleSpaces

See https://ktlint.github.io for documentation.

### NoSemicolons

See https://ktlint.github.io for documentation.

### NoTrailingSpaces

See https://ktlint.github.io for documentation.

### NoUnitReturn

See https://ktlint.github.io for documentation.

### NoUnusedImports

See https://ktlint.github.io for documentation.

### NoWildcardImports

See https://ktlint.github.io for documentation.

### ParameterListWrapping

See https://ktlint.github.io for documentation.

### SpacingAroundColon

See https://ktlint.github.io for documentation.

### SpacingAroundComma

See https://ktlint.github.io for documentation.

### SpacingAroundCurly

See https://ktlint.github.io for documentation.

### SpacingAroundKeyword

See https://ktlint.github.io for documentation.

### SpacingAroundOperators

See https://ktlint.github.io for documentation.

### SpacingAroundRangeOperator

See https://ktlint.github.io for documentation.

### StringTemplate

See https://ktlint.github.io for documentation.
