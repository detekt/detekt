---
title: Empty-blocks Rule Set
sidebar: home_sidebar
keywords: rules, empty-blocks
permalink: empty-blocks.html
toc: true
folder: documentation
---
The empty-blocks ruleset contains rules that will report empty blocks of code
which should be avoided.

### EmptyCatchBlock

Reports empty `catch` blocks. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

#### Configuration options:

* `allowedExceptionNameRegex` (default: `"^(_|(ignore|expected).*)"`)

   ignores exception types which match this regex

### EmptyClassBlock

Reports empty classes. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyDefaultConstructor

Reports empty default constructors. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyDoWhileBlock

Reports empty `do`/`while` loops. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyElseBlock

Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyFinallyBlock

Reports empty `finally` blocks. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyForBlock

Reports empty `for` loops. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyFunctionBlock

Reports empty functions. Empty blocks of code serve no purpose and should be removed.
This rule will not report functions overriding others.

**Severity**: Minor

**Debt**: 5min

#### Configuration options:

* `ignoreOverriddenFunctions` (default: `false`)

   excludes overridden functions with an empty body

### EmptyIfBlock

Reports empty `if` blocks. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyInitBlock

Reports empty `init` expressions. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyKtFile

Reports empty Kotlin (.kt) files. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptySecondaryConstructor

Reports empty secondary constructors. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyWhenBlock

Reports empty `when` expressions. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min

### EmptyWhileBlock

Reports empty `while` expressions. Empty blocks of code serve no purpose and should be removed.

**Severity**: Minor

**Debt**: 5min
