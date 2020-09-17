---
title: Formatting Rule Set
sidebar: home_sidebar
keywords: rules, formatting
permalink: formatting.html
toc: true
folder: documentation
---
This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.

Note: Issues reported by this rule set can only be suppressed on file level (@file:Suppress("detekt.rule").
Note: The formatting rule set is not included in the detekt-cli or gradle plugin.

To enable this rule set, add <i>detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"</i>
to your gradle dependencies or reference the `detekt-formatting`-jar with the `--plugins` option
in the command line interface.

### AnnotationOnSeparateLine

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### AnnotationSpacing

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### ArgumentListWrapping

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### ChainWrapping

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### CommentSpacing

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### EnumEntryNameCase

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### Filename

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### FinalNewline

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

#### Configuration options:

* ``insertFinalNewLine`` (default: ``true``)

   report absence or presence of a newline

### ImportOrdering

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

For defining custom import layout patterns see: https://github.com/pinterest/ktlint/blob/cdf871b6f015359f9a6f02e15ef1b85a6c442437/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/ImportOrderingRule.kt

#### Configuration options:

* ``layout`` (default: ``'idea'``)

   the import ordering layout; use 'ascii', 'idea' or define a custom one

### Indentation

See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.

#### Configuration options:

* ``indentSize`` (default: ``4``)

   indentation size

* ``continuationIndentSize`` (default: ``4``)

   continuation indentation size

### MaximumLineLength

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

#### Configuration options:

* ``maxLineLength`` (default: ``120``)

   maximum line length

### ModifierOrdering

See <a href="https://ktlint.github.io/#rule-modifier-order">ktlint-website</a> for documentation.

### MultiLineIfElse

See <a href="https://ktlint.github.io/#rule-modifier-order">ktlint-website</a> for documentation.

### NoBlankLineBeforeRbrace

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoConsecutiveBlankLines

See <a href="https://ktlint.github.io/#rule-blank">ktlint-website</a> for documentation.

### NoEmptyClassBody

See <a href="https://ktlint.github.io/#rule-empty-class-body">ktlint-website</a> for documentation.

### NoEmptyFirstLineInMethodBlock

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoLineBreakAfterElse

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoLineBreakBeforeAssignment

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoMultipleSpaces

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoSemicolons

See <a href="https://ktlint.github.io/#rule-semi">ktlint-website</a> for documentation.

### NoTrailingSpaces

See <a href="https://ktlint.github.io/#rule-trailing-whitespaces">ktlint-website</a> for documentation.

### NoUnitReturn

See <a href="https://ktlint.github.io/#rule-unit-return">ktlint-website</a> for documentation.

### NoUnusedImports

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoWildcardImports

See <a href="https://ktlint.github.io/#rule-import">ktlint-website</a> for documentation.

### PackageName

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### ParameterListWrapping

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

#### Configuration options:

* ``indentSize`` (default: ``4``)

   indentation size

### SpacingAroundColon

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundComma

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundCurly

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundDot

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundDoubleColon

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundKeyword

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundOperators

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundParens

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundRangeOperator

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingBetweenDeclarationsWithAnnotations

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingBetweenDeclarationsWithComments

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### StringTemplate

See <a href="https://ktlint.github.io/#rule-string-template">ktlint-website</a> for documentation.
