---
title: Formatting Rule Set
sidebar: home_sidebar
keywords: rules, formatting
permalink: formatting.html
toc: true
folder: documentation
---
This rule set provides rules that address formatting issues.

Note: The formatting rule set is not included in the detekt-cli or gradle plugin.

To enable this rule set, add <i>detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"</i>
to your gradle dependencies or reference the `detekt-formatting`-jar with the `--plugins` option
in the command line interface.

### AnnotationOnSeparateLine

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### ChainWrapping

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### CommentSpacing

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### Filename

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### FinalNewline

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### ImportOrdering

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### Indentation

See <a href="https://ktlint.github.io/#rule-indentation">ktlint-website</a> for documentation.

#### Configuration options:

* `indentSize` (default: `4`)

   indentation size

* `continuationIndentSize` (default: `4`)

   continuation indentation size

### MaximumLineLength

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

#### Configuration options:

* `maxLineLength` (default: `120`)

   maximum line length

### ModifierOrdering

See <a href="https://ktlint.github.io/#rule-modifier-order">ktlint-website</a> for documentation.

### NoBlankLineBeforeRbrace

See <a href="https://ktlint.github.io">ktlint-website</a> for documentation.

### NoConsecutiveBlankLines

See <a href="https://ktlint.github.io/#rule-blank">ktlint-website</a> for documentation.

### NoEmptyClassBody

See <a href="https://ktlint.github.io/#rule-empty-class-body">ktlint-website</a> for documentation.

### NoItParamInMultilineLambda

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

* `indentSize` (default: `4`)

   indentation size

### SpacingAroundColon

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundComma

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundCurly

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundDot

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundKeyword

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundOperators

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundParens

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### SpacingAroundRangeOperator

See <a href="https://ktlint.github.io/#rule-spacing">ktlint-website</a> for documentation.

### StringTemplate

See <a href="https://ktlint.github.io/#rule-string-template">ktlint-website</a> for documentation.
