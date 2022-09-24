---
title: Formatting Rule Set
sidebar: home_sidebar
keywords: [rules, formatting]
permalink: formatting.html
toc: true
folder: documentation
---
This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.

Note: Issues reported by this rule set can only be suppressed on file level (`@file:Suppress("detekt.rule")`).
Note: The formatting rule set is not included by default in the detekt-cli or gradle plugin.

To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"`
to your gradle dependencies or reference the `detekt-formatting`-jar with the `--plugins` option
in the command line interface.

See the [config.yml](https://github.com/detekt/detekt/blob/main/detekt-formatting/src/main/resources/config/config.yml)
file for all `detekt-formatting` configuration options and their default values.

To enable\disable rule add `formatting:` section (from the above config file) to your custom detekt config file.

### AnnotationOnSeparateLine

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

### AnnotationSpacing

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

### ArgumentListWrapping

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### BlockCommentInitialStarAlignment

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### ChainWrapping

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### CommentSpacing

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### CommentWrapping

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### DiscouragedCommentLocation

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### EnumEntryNameCase

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

### Filename

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

This rules overlaps with [naming&gt;MatchingDeclarationName](https://detekt.dev/naming.html#matchingdeclarationname)
from the standard rules, make sure to enable just one.

**Active by default**: Yes - Since v1.0.0

### FinalNewline

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

This rules overlaps with [style&gt;NewLineAtEndOfFile](https://detekt.dev/style.html#newlineatendoffile)
from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``insertFinalNewLine`` (default: ``true``)

  report absence or presence of a newline

### FunKeywordSpacing

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### FunctionTypeReferenceSpacing

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### ImportOrdering

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

For defining import layout patterns see the [KtLint Source Code](https://github.com/pinterest/ktlint/blob/a6ca5b2edf95cc70a138a9470cfb6c4fd5d9d3ce/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt)

**Active by default**: Yes - Since v1.19.0

#### Configuration options:

* ``layout`` (default: ``'*,java.**,javax.**,kotlin.**,^'``) (android default: ``'*'``)

  the import ordering layout

### Indentation

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.19.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ~~``continuationIndentSize``~~ (default: ``4``)

  **Deprecated**: `continuationIndentSize` is ignored by KtLint and will have no effect

  continuation indentation size

### KdocWrapping

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### MaximumLineLength

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

This rules overlaps with [style&gt;MaxLineLength](https://detekt.dev/style.html#maxlinelength)
from the standard rules, make sure to enable just one or keep them aligned. The pro of this rule is that it can
auto-correct the issue.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``ignoreBackTickedIdentifier`` (default: ``false``)

  ignore back ticked identifier

### ModifierListSpacing

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### ModifierOrdering

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

This rules overlaps with [style&gt;ModifierOrder](https://detekt.dev/style.html#modifierorder)
from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.

**Active by default**: Yes - Since v1.0.0

### MultiLineIfElse

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

### NoBlankLineBeforeRbrace

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoConsecutiveBlankLines

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoEmptyClassBody

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoEmptyFirstLineInMethodBlock

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

### NoLineBreakAfterElse

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoLineBreakBeforeAssignment

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoMultipleSpaces

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoSemicolons

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoTrailingSpaces

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoUnitReturn

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoUnusedImports

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoWildcardImports

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``packagesToUseImportOnDemandProperty`` (default: ``'java.util.*,kotlinx.android.synthetic.**'``)

  Defines allowed wildcard imports

### PackageName

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

### ParameterListWrapping

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ~~``indentSize``~~ (default: ``4``)

  **Deprecated**: `indentSize` is ignored by KtLint and will have no effect

  indentation size

### SpacingAroundAngleBrackets

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: No

### SpacingAroundColon

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundComma

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundCurly

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundDot

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundDoubleColon

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: No

### SpacingAroundKeyword

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundOperators

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundParens

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundRangeOperator

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundUnaryOperator

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: No

### SpacingBetweenDeclarationsWithAnnotations

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: No

### SpacingBetweenDeclarationsWithComments

See [ktlint-readme](https://github.com/pinterest/ktlint#spacing) for documentation.

**Active by default**: No

### StringTemplate

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.0.0

### TrailingComma

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: No

#### Configuration options:

* ``allowTrailingComma`` (default: ``false``)

  Defines whether a trailing comma (or no trailing comma) should be enforced on the defining side

* ``allowTrailingCommaOnCallSite`` (default: ``false``)

  Defines whether a trailing comma (or no trailing comma) should be enforced on the calling side

### TypeArgumentListSpacing

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### UnnecessaryParenthesesBeforeTrailingLambda

See [ktlint-readme](https://github.com/pinterest/ktlint#experimental-rules) for documentation.

**Active by default**: No

### Wrapping

See [ktlint-readme](https://github.com/pinterest/ktlint#standard-rules) for documentation.

**Active by default**: Yes - Since v1.20.0
