---
title: Formatting Rule Set
sidebar: home_sidebar
keywords: [rules, formatting]
permalink: formatting.html
toc: true
folder: documentation
---
This rule set provides wrappers for rules implemented by ktlint - https://ktlint.github.io/.

**Note: The `formatting` rule set is not included in the detekt-cli or Gradle plugin.**

To enable this rule set, add `detektPlugins "io.gitlab.arturbosch.detekt:detekt-formatting:$version"`
to your gradle `dependencies` or reference the `detekt-formatting`-jar with the `--plugins` option
in the command line interface.

Note: Issues reported by this rule set can only be suppressed on file level (`@file:Suppress("detekt.rule")`).

### AnnotationOnSeparateLine

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#annotation-formatting) for documentation.

**Active by default**: Yes - Since v1.22.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### AnnotationSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#annotation-spacing) for documentation.

**Active by default**: Yes - Since v1.22.0

### ArgumentListWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#argument-list-wrapping) for documentation.

**Active by default**: Yes - Since v1.22.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``ignoreRuleParameterThreshold`` (default: ``8``)

  parameter threshold to ignore rule

### BackingPropertyNaming

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#backing-property-naming)
for documentation.

**Active by default**: Yes - Since v2.0.0

### BinaryExpressionWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#binary-expression-wrapping) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``indentSize`` (default: ``4``)

  indentation size

### BlankLineBeforeDeclaration

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#blank-line-before-declarations) for
documentation.

**Active by default**: Yes - Since v2.0.0

### BlankLineBetweenWhenConditions

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/experimental/#blank-lines-between-when-conditions)
for documentation.

**Active by default**: No

#### Configuration options:

* ``lineBreakAfterWhenEntries`` (default: ``true``)

  require line breaks after multiline entries

### BlockCommentInitialStarAlignment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#block-comment-initial-star-alignment) for
documentation.

**Active by default**: Yes - Since v1.23.0

### ChainMethodContinuation

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#chain-method-continuation)
for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``forceMultilineWhenChainOperatorCountGreaterOrEqualThan`` (default: ``4``)

  chain operator count means multiline threshold

### ChainWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#chain-wrapping) for documentation.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### ClassName

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#class-naming) for
documentation.

**Active by default**: No

### ClassSignature

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#class-signature) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### CommentSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#comment-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### CommentWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#comment-wrapping) for documentation.

**Active by default**: Yes - Since v1.23.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### ConditionWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#condition-wrapping) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### ContextReceiverListWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#context-receiver-list-wrapping) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``indentSize`` (default: ``4``)

  indentation size

### ContextReceiverMapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#content-receiver-wrapping) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``indentSize`` (default: ``4``)

  indentation size

### EnumEntryNameCase

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#enum-entry) for documentation.

**Active by default**: Yes - Since v1.22.0

**Aliases**: EnumEntryName

#### Configuration options:

* ``enumEntryNameCasing`` (default: ``'upper_or_camel_cases'``)

  enum entry naming casing

### EnumWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#enum-wrapping) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### ExpressionOperandWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/experimental/#expression-operand-wrapping) for
documentation.

**Active by default**: No

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### Filename

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#file-name) for documentation.

This rules overlaps with [naming>MatchingDeclarationName](https://detekt.dev/naming.html#matchingdeclarationname)
from the standard rules, make sure to enable just one.

**Active by default**: Yes - Since v1.0.0

### FinalNewline

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#final-newline) for documentation.

This rules overlaps with [style>NewLineAtEndOfFile](https://detekt.dev/style.html#newlineatendoffile)
from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``insertFinalNewLine`` (default: ``true``)

  report absence or presence of a newline

### FunKeywordSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#fun-keyword-spacing) for documentation.

**Active by default**: Yes - Since v1.23.0

### FunctionExpressionBody

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-expression-body)
for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### FunctionLiteral

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-literal) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### FunctionName

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-naming) for
documentation.

**Active by default**: No

### FunctionReturnTypeSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-return-type-spacing) for
documentation.

**Active by default**: Yes - Since v1.23.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### FunctionSignature

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-signature) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``forceMultilineWhenParameterCountGreaterOrEqualThan`` (default: ``2147483647``)

  parameter count means multiline threshold

* ``functionBodyExpressionWrapping`` (default: ``'default'``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``indentSize`` (default: ``4``)

  indentation size

### FunctionStartOfBodySpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-start-of-body-spacing) for
documentation.

**Active by default**: Yes - Since v1.23.0

### FunctionTypeModifierSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-type-modifier-spacing)
for documentation.

**Active by default**: Yes - Since v2.0.0

### FunctionTypeReferenceSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#function-type-reference-spacing) for
documentation.

**Active by default**: Yes - Since v1.23.0

### IfElseBracing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#if-else-bracing) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### IfElseWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#if-else-wrapping) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### ImportOrdering

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#import-ordering) for documentation.

For defining import layout patterns see the [KtLint Source Code](https://github.com/pinterest/ktlint/blob/0.50.0/ktlint-ruleset-standard/src/main/kotlin/com/pinterest/ktlint/ruleset/standard/rules/ImportOrderingRule.kt)

**Active by default**: Yes - Since v1.19.0

#### Configuration options:

* ``layout`` (default: ``'*,java.**,javax.**,kotlin.**,^'``) (android default: ``'*'``)

  the import ordering layout

### Indentation

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#indentation) for documentation.

**Active by default**: Yes - Since v1.19.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``indentWhenArrowOnNewLine`` (default: ``false``)

  indent when arrow on new line

### Kdoc

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/experimental/#kdoc) for documentation.

**Active by default**: No

### KdocWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#kdoc-wrapping) for documentation.

**Active by default**: Yes - Since v1.23.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### MaximumLineLength

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#max-line-length) for documentation.

This rules overlaps with [style>MaxLineLength](https://detekt.dev/style.html#maxlinelength)
from the standard rules, make sure to enable just one or keep them aligned.

**Active by default**: Yes - Since v1.0.0

**Aliases**: MaxLineLength

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``ignoreBackTickedIdentifier`` (default: ``false``)

  ignore back ticked identifier

### MixedConditionOperators

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/experimental/) for documentation.

**Active by default**: No

### ModifierListSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#modifier-list-spacing) for documentation.

**Active by default**: Yes - Since v1.23.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### ModifierOrdering

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#modifier-order) for documentation.

This rules overlaps with [style>ModifierOrder](https://detekt.dev/style.html#modifierorder)
from the standard rules, make sure to enable just one. The pro of this rule is that it can auto-correct the issue.

**Active by default**: Yes - Since v1.0.0

### MultiLineIfElse

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#multiline-if-else) for documentation.

**Active by default**: Yes - Since v1.22.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### MultilineExpressionWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#multiline-expression-wrapping) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``functionBodyExpressionWrapping`` (default: ``'multiline'``)

  function body expression wrapping

### MultilineLoop

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#multiline-loop) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### NoBlankLineBeforeRbrace

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-blank-lines-before) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoBlankLineInList

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-blank-lines-in-list) for documentation.

**Active by default**: Yes - Since v2.0.0

### NoBlankLinesInChainedMethodCalls

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-blank-lines-in-chained-method-calls) for
documentation.

**Active by default**: Yes - Since v1.22.0

### NoConsecutiveBlankLines

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-consecutive-blank-lines) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoConsecutiveComments

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-consecutive-comments) for documentation.

**Active by default**: Yes - Since v2.0.0

### NoEmptyClassBody

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-empty-class-bodies) for documentation.

This rules overlaps with [empty-blocks>EmptyClassBlock](https://detekt.dev/empty-blocks.html#emptyclassblock)
from the standard rules, make sure to enable just one.

**Active by default**: Yes - Since v1.0.0

### NoEmptyFile

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-empty-file) for documentation.

**Active by default**: Yes - Since v2.0.0

### NoEmptyFirstLineInClassBody

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-empty-first-line-at-start-in-class-body)
for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### NoEmptyFirstLineInMethodBlock

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-leading-empty-lines-in-method-blocks) for
documentation.

**Active by default**: Yes - Since v1.22.0

### NoLineBreakAfterElse

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-line-break-after-else) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoLineBreakBeforeAssignment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-line-break-before-assignment) for
documentation.

**Active by default**: Yes - Since v1.0.0

### NoMultipleSpaces

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-multi-spaces) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoSemicolons

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-semicolons) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoSingleLineBlockComment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-single-line-block-comment) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### NoTrailingSpaces

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-trailing-whitespaces) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoUnitReturn

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-unit-as-return-type) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoUnusedImports

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-unused-imports) for documentation.

**Active by default**: Yes - Since v1.0.0

### NoWildcardImports

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#no-wildcard-imports) for documentation.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``packagesToUseImportOnDemandProperty`` (default: ``'java.util.*,kotlinx.android.synthetic.**'``)

  Defines allowed wildcard imports

### NullableTypeSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#nullable-type-spacing) for
documentation.

**Active by default**: Yes - Since v1.23.0

### PackageName

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#package-name) for
documentation.

**Active by default**: Yes - Since v1.22.0

### ParameterListSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#parameter-list-spacing) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### ParameterListWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#parameter-list-wrapping) for documentation.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

* ``indentSize`` (default: ``4``)

  indentation size

### ParameterWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#parameter-wrapping) for documentation.

**Active by default**: Yes - Since v1.23.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### PropertyName

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#property-naming) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``constantNamingStyle`` (default: ``'screaming_snake_case'``)

  The naming style ('screaming_snake_case', or 'pascal_case') to be applied on constant properties.

### PropertyWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#property-wrapping) for documentation.

**Active by default**: Yes - Since v1.23.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length

### SpacingAroundAngleBrackets

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#angle-bracket-spacing) for documentation.

**Active by default**: Yes - Since v1.22.0

### SpacingAroundColon

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#colon-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundComma

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#comma-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundCurly

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#curly-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### SpacingAroundDot

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#dot-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundDoubleColon

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#double-colon-spacing) for documentation.

**Active by default**: Yes - Since v1.22.0

### SpacingAroundKeyword

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#keyword-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundOperators

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#operator-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundParens

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#parenthesis-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundRangeOperator

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#range-spacing) for documentation.

**Active by default**: Yes - Since v1.0.0

### SpacingAroundSquareBrackets

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/experimental/#square-brackets-spacing) for
documentation.

**Active by default**: No

### SpacingAroundUnaryOperator

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#unary-operator-spacing) for documentation.

**Active by default**: Yes - Since v1.22.0

### SpacingBetweenDeclarationsWithAnnotations

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#blank-line-between-declarations-with-annotations)
for documentation.

**Active by default**: Yes - Since v1.22.0

### SpacingBetweenDeclarationsWithComments

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#blank-line-between-declaration-with-comments)
for documentation.

**Active by default**: Yes - Since v1.22.0

### SpacingBetweenFunctionNameAndOpeningParenthesis

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#spacing-between-function-name-and-opening-parenthesis) for
documentation.

**Active by default**: Yes - Since v1.23.0

### StatementWrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#statement-wrapping) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### StringTemplate

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#string-template) for documentation.

**Active by default**: Yes - Since v1.0.0

### StringTemplateIndent

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#string-template-indent) for documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### TrailingCommaOnCallSite

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#trailing-comma-on-call-site) for documentation.

The default config comes from ktlint and follows these conventions:
- [Kotlin coding convention](https://kotlinlang.org/docs/coding-conventions.html#trailing-commas) recommends
trailing comma encourage the use of trailing commas at the declaration site and
leaves it at your discretion for the call site.
- [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide) does not include
trailing comma usage yet.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``useTrailingCommaOnCallSite`` (default: ``true``) (android default: ``false``)

  Defines whether trailing commas are required (true) or forbidden (false) at call sites

### TrailingCommaOnDeclarationSite

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#trailing-comma-on-declaration-site) for documentation.

The default config comes from ktlint and follows these conventions:
- [Kotlin coding convention](https://kotlinlang.org/docs/coding-conventions.html#trailing-commas) recommends
trailing comma encourage the use of trailing commas at the declaration site and
leaves it at your discretion for the call site.
- [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide) does not include
trailing comma usage yet.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``useTrailingCommaOnDeclarationSite`` (default: ``true``) (android default: ``false``)

  Defines whether trailing commas are required (true) or forbidden (false) at declaration sites

### TryCatchFinallySpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#try-catch-finally-spacing) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### TypeArgumentComment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/) for
documentation.

**Active by default**: Yes - Since v2.0.0

### TypeArgumentListSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#type-argument-list-spacing) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### TypeParameterComment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/) for
documentation.

**Active by default**: Yes - Since v2.0.0

### TypeParameterListSpacing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#type-parameter-list-spacing) for
documentation.

**Active by default**: Yes - Since v2.0.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### UnnecessaryParenthesesBeforeTrailingLambda

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#unnecessary-parenthesis-before-trailing-lambda)
for documentation.

**Active by default**: Yes - Since v1.23.0

### ValueArgumentComment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/) for
documentation.

**Active by default**: Yes - Since v2.0.0

### ValueParameterComment

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/) for
documentation.

**Active by default**: Yes - Since v2.0.0

### WhenEntryBracing

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/experimental/#when-entry-bracing) for
documentation.

**Active by default**: No

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

### Wrapping

See [ktlint docs](https://pinterest.github.io/ktlint/1.7.1/rules/standard/#wrapping) for documentation.

**Active by default**: Yes - Since v1.20.0

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``maxLineLength`` (default: ``120``) (android default: ``100``)

  maximum line length
