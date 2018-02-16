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

#### Configuration options:

* `allowedExceptionNameRegex` (default: `"^(ignore|expected).*"`)

   ignores exception types which match this regex

### EmptyClassBlock

Reports empty classes. Empty blocks of code serve no purpose and should be removed.

### EmptyDefaultConstructor

Reports empty default constructors. Empty blocks of code serve no purpose and should be removed.

### EmptyDoWhileBlock

Reports empty `do`/`while` loops. Empty blocks of code serve no purpose and should be removed.

### EmptyElseBlock

Reports empty `else` blocks. Empty blocks of code serve no purpose and should be removed.

### EmptyFinallyBlock

Reports empty `finally` blocks. Empty blocks of code serve no purpose and should be removed.

### EmptyForBlock

Reports empty `for` loops. Empty blocks of code serve no purpose and should be removed.

### EmptyFunctionBlock

Reports empty functions. Empty blocks of code serve no purpose and should be removed.
This rule will not report functions overriding others.

### EmptyIfBlock

Reports empty `if` blocks. Empty blocks of code serve no purpose and should be removed.

### EmptyInitBlock

Reports empty `init` expressions. Empty blocks of code serve no purpose and should be removed.

### EmptyKtFile

Reports empty Kotlin (.kt) files. Empty blocks of code serve no purpose and should be removed.

### EmptySecondaryConstructor

Reports empty secondary constructors. Empty blocks of code serve no purpose and should be removed.

### EmptyWhenBlock

Reports empty `when` expressions. Empty blocks of code serve no purpose and should be removed.

### EmptyWhileBlock

Reports empty `while` expressions. Empty blocks of code serve no purpose and should be removed.
