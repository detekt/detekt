---
title: Empty-blocks Rule Set
sidebar: home_sidebar
keywords: [rules, empty-blocks]
permalink: empty-blocks.html
toc: true
folder: documentation
---
The empty-blocks ruleset contains rules that will report empty blocks of code
which should be avoided.

### EmptyCatchBlock

Reports empty `catch` blocks. Empty catch blocks indicate that an exception is ignored and not handled.
In case exceptions are ignored intentionally, this should be made explicit
by using the specified names in the `allowedExceptionNameRegex`.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``allowedExceptionNameRegex`` (default: ``'_|(ignore|expected).*'``)

  ignores exception types which match this regex

### EmptyClassBlock

Reports empty classes. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyDefaultConstructor

Reports empty default constructors. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyDoWhileBlock

Reports empty `do`/`while` loops. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyElseBlock

Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyFinallyBlock

Reports empty `finally` blocks. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyForBlock

Reports empty `for` loops. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyFunctionBlock

Reports empty functions. Empty blocks of code serve no purpose and should be removed.
This rule will not report functions with the override modifier that have a comment as their only body contents
(e.g., a `// no-op` comment in an unused listener function).

Set the `ignoreOverridden` parameter to `true` to exclude all functions which are overriding other
functions from the superclass or from an interface (i.e., functions declared with the override modifier).

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``ignoreOverridden`` (default: ``false``)

  Excludes all the overridden functions

### EmptyIfBlock

Reports empty `if` blocks. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyInitBlock

Reports empty `init` expressions. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyKotlinFile

Reports empty Kotlin (.kt, .kts) files. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptySecondaryConstructor

Reports empty secondary constructors. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyTryBlock

Reports empty `try` blocks. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.6.0

### EmptyWhenBlock

Reports empty `when` expressions. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0

### EmptyWhileBlock

Reports empty `while` expressions. Empty blocks of code serve no purpose and should be removed.

**Active by default**: Yes - Since v1.0.0
