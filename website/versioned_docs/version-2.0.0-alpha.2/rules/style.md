---
title: Style Rule Set
sidebar: home_sidebar
keywords: [rules, style]
permalink: style.html
toc: true
folder: documentation
---
Rule Set ID: `style`

The Style ruleset provides rules that assert the style of the code.
This will help keep code in line with the given
code style guidelines.

### AbstractClassCanBeConcreteClass

This rule inspects `abstract` classes. Abstract classes which do not define any `abstract` members should instead be
refactored into concrete classes.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members

    val i: Int = 0
    fun f() { }
}
```

#### Compliant Code:

```kotlin
interface OnlyAbstractMembersInInterface {
val i: Int
fun f()
}

class OnlyConcreteMembersInClass {
val i: Int = 0
fun f() { }
}
```

### AbstractClassCanBeInterface

This rule inspects `abstract` classes. In case an `abstract class` does not define any
`abstract` members, it should instead be refactored into an interface.

**Active by default**: Yes - Since v1.23.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members

    abstract val i: Int
    abstract fun f()
}
```

#### Compliant Code:

```kotlin
interface Interface {
val i: Int
fun f()
}

abstract class NonAbstractMembersInAbstractClass {

    abstract val i: Int
    fun f() {
    }
}
```

### AlsoCouldBeApply

Detects when an `also` block contains only `it`-started expressions.

By refactoring the `also` block to an `apply` block makes it so that all `it`s can be removed
thus making the block more concise and easier to read.

**Active by default**: No

#### Noncompliant Code:

```kotlin
Buzz().also {
it.init()
it.block()
}
```

#### Compliant Code:

```kotlin
Buzz().apply {
init()
block()
}

// Also compliant
fun foo(a: Int): Int {
return a.also { println(it) }
}
```

### BracesOnIfStatements

This rule detects `if` statements which do not comply with the specified rules.
Keeping braces consistent will improve readability and avoid possible errors.

The available options are:
* `always`: forces braces on all `if` and `else` branches in the whole codebase.
* `consistent`: ensures that braces are consistent within each `if`-`else if`-`else` chain.
If there's a brace on one of the branches, all branches should have it.
* `necessary`: forces no braces on any `if` and `else` branches in the whole codebase
except where necessary for multi-statement branches.
* `never`: forces no braces on any `if` and `else` branches in the whole codebase.

Single-line if-statement has no line break (\n):
```kotlin
if (a) b else c
```
Multi-line if-statement has at least one line break (\n):
```kotlin
if (a) b
else c
```

**Active by default**: No

#### Configuration options:

* ``singleLine`` (default: ``'never'``)

  single-line braces policy

* ``multiLine`` (default: ``'always'``)

  multi-line braces policy

#### Noncompliant Code:

```kotlin
// singleLine = 'never'
if (a) { b } else { c }

if (a) { b } else c

if (a) b else { c; d }

// multiLine = 'never'
if (a) {
b
} else {
c
}

// singleLine = 'always'
if (a) b else c

if (a) { b } else c

// multiLine = 'always'
if (a) {
b
} else
c

// singleLine = 'consistent'
if (a) b else { c }
if (a) b else if (c) d else { e }

// multiLine = 'consistent'
if (a)
b
else {
c
}

// singleLine = 'necessary'
if (a) { b } else { c; d }

// multiLine = 'necessary'
if (a) {
b
c
} else if (d) {
e
} else {
f
}
```

#### Compliant Code:

```kotlin
// singleLine = 'never'
if (a) b else c

// multiLine = 'never'
if (a)
b
else
c

// singleLine = 'always'
if (a) { b } else { c }

if (a) { b } else if (c) { d }

// multiLine = 'always'
if (a) {
b
} else {
c
}

if (a) {
b
} else if (c) {
d
}

// singleLine = 'consistent'
if (a) b else c

if (a) { b } else { c }

if (a) { b } else { c; d }

// multiLine = 'consistent'
if (a) {
b
} else {
c
}

if (a) b
else c

// singleLine = 'necessary'
if (a) b else { c; d }

// multiLine = 'necessary'
if (a) {
b
c
} else if (d)
e
else
f
```

### BracesOnWhenStatements

This rule detects `when` statements which do not comply with the specified policy.
Keeping braces consistent will improve readability and avoid possible errors.

Single-line `when` statement is:
a `when` where each of the branches are single-line (has no line breaks `\n`).

Multi-line `when` statement is:
a `when` where at least one of the branches is multi-line (has a break line `\n`).

Available options are:
* `never`: forces no braces on any branch.
_Tip_: this is very strict, it will force a simple expression, like a single function call / expression.
Extracting a function for "complex" logic is one way to adhere to this policy.
* `necessary`: forces no braces on any branch except where necessary for multi-statement branches.
* `consistent`: ensures that braces are consistent within `when` statement.
If there are braces on one of the branches, all branches should have it.
* `always`: forces braces on all branches.

**Active by default**: No

#### Configuration options:

* ``singleLine`` (default: ``'necessary'``)

  single-line braces policy

* ``multiLine`` (default: ``'consistent'``)

  multi-line braces policy

#### Noncompliant Code:

```kotlin
// singleLine = 'never'
when (a) {
1 -> { f1() } // Not allowed.
2 -> f2()
}
// multiLine = 'never'
when (a) {
1 -> { // Not allowed.
f1()
}
2 -> f2()
}
// singleLine = 'necessary'
when (a) {
1 -> { f1() } // Unnecessary braces.
2 -> f2()
}
// multiLine = 'necessary'
when (a) {
1 -> { // Unnecessary braces.
f1()
}
2 -> f2()
}

// singleLine = 'consistent'
when (a) {
1 -> { f1() }
2 -> f2()
}
// multiLine = 'consistent'
when (a) {
1 ->
f1() // Missing braces.
2 -> {
f2()
f3()
}
}

// singleLine = 'always'
when (a) {
1 -> { f1() }
2 -> f2() // Missing braces.
}
// multiLine = 'always'
when (a) {
1 ->
f1() // Missing braces.
2 -> {
f2()
f3()
}
}
```

#### Compliant Code:

```kotlin
// singleLine = 'never'
when (a) {
1 -> f1()
2 -> f2()
}
// multiLine = 'never'
when (a) {
1 ->
f1()
2 -> f2()
}
// singleLine = 'necessary'
when (a) {
1 -> f1()
2 -> { f2(); f3() } // Necessary braces because of multiple statements.
}
// multiLine = 'necessary'
when (a) {
1 ->
f1()
2 -> { // Necessary braces because of multiple statements.
f2()
f3()
}
}

// singleLine = 'consistent'
when (a) {
1 -> { f1() }
2 -> { f2() }
}
when (a) {
1 -> f1()
2 -> f2()
}
// multiLine = 'consistent'
when (a) {
1 -> {
f1()
}
2 -> {
f2()
f3()
}
}

// singleLine = 'always'
when (a) {
1 -> { f1() }
2 -> { f2() }
}
// multiLine = 'always'
when (a) {
1 -> {
f1()
}
2 -> {
f2()
f3()
}
}
```

### CanBeNonNullable

This rule inspects variables marked as nullable and reports which could be
declared as non-nullable instead.

It's preferred to not have functions that do "nothing".
A function that does nothing when the value is null hides the logic,
so it should not allow null values in the first place.
It is better to move the null checks up around the calls,
instead of having it inside the function.

This could lead to less nullability overall in the codebase.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
class A {
var a: Int? = 5

    fun foo() {
        a = 6
    }
}

class A {
val a: Int?
get() = 5
}

fun foo(a: Int?) {
val b = a!! + 2
}

fun foo(a: Int?) {
if (a != null) {
println(a)
}
}

fun foo(a: Int?) {
if (a == null) return
println(a)
}
```

#### Compliant Code:

```kotlin
class A {
var a: Int = 5

    fun foo() {
        a = 6
    }
}

class A {
val a: Int
get() = 5
}

fun foo(a: Int) {
val b = a + 2
}

fun foo(a: Int) {
println(a)
}
```

### CascadingCallWrapping

Requires that all chained calls are placed on a new line if a preceding one is.

**Active by default**: No

#### Configuration options:

* ``includeElvis`` (default: ``true``)

  require trailing elvis expressions to be wrapped on a new line

#### Noncompliant Code:

```kotlin
foo()
.bar().baz()
```

#### Compliant Code:

```kotlin
foo().bar().baz()

foo()
.bar()
.baz()
```

### ClassOrdering

This rule ensures class contents are ordered as follows as recommended by the Kotlin
[Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html#class-layout):
- Property declarations and initializer blocks
- Secondary constructors
- Method declarations
- Companion object

**Active by default**: No

#### Noncompliant Code:

```kotlin
class OutOfOrder {
companion object {
const val IMPORTANT_VALUE = 3
}

    fun returnX(): Int {
        return x
    }

    private val x = 2
}
```

#### Compliant Code:

```kotlin
class InOrder {
private val x = 2

    fun returnX(): Int {
        return x
    }

    companion object {
        const val IMPORTANT_VALUE = 3
    }
}
```

### CollapsibleIfStatements

This rule detects `if` statements which can be collapsed. This can reduce nesting and help improve readability.

However, carefully consider whether merging the if statements actually improves readability, as collapsing the
statements may hide some edge cases from the reader.

**Active by default**: No

#### Noncompliant Code:

```kotlin
val i = 1
if (i > 0) {
if (i < 5) {
println(i)
}
}
```

#### Compliant Code:

```kotlin
val i = 1
if (i > 0 && i < 5) {
println(i)
}
```

### DataClassContainsFunctions

This rule reports functions inside data classes which have not been marked as a conversion function.

Data classes should mainly be used to store data. This rule assumes that they should not contain any extra functions
aside functions that help with converting objects from/to one another.
Data classes will automatically have a generated `equals`, `toString` and `hashCode` function by the compiler.

**Active by default**: No

#### Configuration options:

* ``conversionFunctionPrefix`` (default: ``['to']``)

  allowed conversion function names

* ``allowOperators`` (default: ``false``)

  allows overloading an operator

#### Noncompliant Code:

```kotlin
data class DataClassWithFunctions(val i: Int) {
fun foo() { }
}
```

### DataClassShouldBeImmutable

This rule reports mutable properties inside data classes.

Data classes should mainly be used to store immutable data. This rule assumes that they should not contain any
mutable properties.

**Active by default**: No

#### Noncompliant Code:

```kotlin
data class MutableDataClass(var i: Int) {
var s: String? = null
}
```

#### Compliant Code:

```kotlin
data class ImmutableDataClass(
val i: Int,
val s: String?
)
```

### DestructuringDeclarationWithTooManyEntries

Destructuring declarations with too many entries are hard to read and understand.
To increase readability they should be refactored to reduce the number of entries or avoid using a destructuring
declaration.

**Active by default**: Yes - Since v1.21.0

#### Configuration options:

* ``maxDestructuringEntries`` (default: ``3``)

  maximum allowed elements in a destructuring declaration

#### Noncompliant Code:

```kotlin
data class TooManyElements(val a: Int, val b: Int, val c: Int, val d: Int)
val (a, b, c, d) = TooManyElements(1, 2, 3, 4)
```

#### Compliant Code:

```kotlin
data class FewerElements(val a: Int, val b: Int, val c: Int)
val (a, b, c) = TooManyElements(1, 2, 3)
```

### DoubleNegativeExpression

Detects expressions with two or more calls of operator `not` could be simplified.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
isValid.not().not()
!isValid.not()
!!isValid
```

#### Compliant Code:

```kotlin
isValid
```

### DoubleNegativeLambda

Detects negation in lambda blocks where the function name is also in the negative (like `takeUnless`).
A double negative is harder to read than a positive. In particular, if there are multiple conditions with `&&` etc. inside
the lambda, then the reader may need to unpack these using DeMorgan's laws. Consider rewriting the lambda to use a positive version
of the function (like `takeIf`).

**Active by default**: No

#### Configuration options:

* ``negativeFunctions`` (default: ``['takeUnless', 'none']``)

  Function names expressed in the negative that can form double negatives with their lambda blocks. These are grouped together with a recommendation to use a positive counterpart, or `null` if this is unknown.

* ``negativeFunctionNameParts`` (default: ``['not', 'non']``)

  Function name parts to look for in the lambda block when deciding if the lambda contains a negative.

#### Noncompliant Code:

```kotlin
fun Int.evenOrNull() = takeUnless { it % 2 != 0 }
```

#### Compliant Code:

```kotlin
fun Int.evenOrNull() = takeIf { it % 2 == 0 }
```

### EqualsNullCall

To compare an object with `null` prefer using `==`. This rule detects and reports instances in the code where the
`equals()` method is used to compare a value with `null`.

**Active by default**: Yes - Since v1.2.0

#### Noncompliant Code:

```kotlin
fun isNull(str: String) = str.equals(null)
```

#### Compliant Code:

```kotlin
fun isNull(str: String) = str == null
```

### EqualsOnSignatureLine

Requires that the equals sign, when used for an expression style function, is on the same line as the
rest of the function signature.

**Active by default**: No

#### Noncompliant Code:

```kotlin
fun stuff(): Int
= 5

fun <V> foo(): Int where V : Int
= 5
```

#### Compliant Code:

```kotlin
fun stuff() = 5

fun stuff() =
foo.bar()

fun <V> foo(): Int where V : Int = 5
```

### ExplicitCollectionElementAccessMethod

In Kotlin functions `get` or `set` can be replaced with the shorter operator — `[]`,
see [Indexed access operator](https://kotlinlang.org/docs/operator-overloading.html#indexed-access-operator).
Prefer the usage of the indexed access operator `[]` for map or list element access or insert methods.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
val map = mutableMapOf<String, String>()
map.put("key", "value")
val value = map.get("key")
```

#### Compliant Code:

```kotlin
val map = mutableMapOf<String, String>()
map["key"] = "value"
val value = map["key"]
```

### ExplicitItLambdaMultipleParameters

Lambda expressions are one of the core features of the language. They often include very small chunks of
code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
to make code more concise. It fits most use cases, but when faced larger or nested chunks of code,
you might want to add an explicit name for the parameter. Naming it just `it` is meaningless and only
makes your code misleading, especially when dealing with nested functions.

**Active by default**: Yes - Since v1.21.0

#### Noncompliant Code:

```kotlin
collection.zipWithNext { it, next -> Pair(it, next) }
```

#### Compliant Code:

```kotlin
// Lambdas with multiple parameter should be named clearly, using it for one of them can be confusing
collection.zipWithNext { prev, next ->
Pair(prev, next)
}
```

### ExplicitItLambdaParameter

Lambda expressions are one of the core features of the language. They often include very small chunks of
code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
to make code more concise. It fits most use cases, but when faced larger or nested chunks of code,
you might want to add an explicit name for the parameter. Naming it just `it` is meaningless and only
makes your code misleading, especially when dealing with nested functions.

**Active by default**: Yes - Since v1.21.0

#### Noncompliant Code:

```kotlin
a?.let { it -> it.plus(1) }
foo.flatMapObservable { it -> Observable.fromIterable(it) }
listOfPairs.map(::second).forEach { it ->
it.execute()
}
```

#### Compliant Code:

```kotlin
a?.let { it.plus(1) } // Much better to use implicit it
a?.let { value: Int -> value.plus(1) } // Better as states the type more clearly
foo.flatMapObservable(Observable::fromIterable) // Here we can have a method reference

// For multiline blocks it is usually better come up with a clear and more meaningful name
listOfPairs.map(::second).forEach { apiRequest ->
apiRequest.execute()
}
```

### ExpressionBodySyntax

Functions which only contain a `return` statement can be collapsed to an expression body. This shortens and
cleans up the code.

**Active by default**: No

#### Configuration options:

* ``includeLineWrapping`` (default: ``false``)

  include return statements with line wraps in it

#### Noncompliant Code:

```kotlin
fun stuff(): Int {
return 5
}
```

#### Compliant Code:

```kotlin
fun stuff() = 5

fun stuff() {
return
moreStuff()
.getStuff()
.stuffStuff()
}
```

### ForbiddenAnnotation

This rule allows to set a list of forbidden annotations. This can be used to discourage the use
of language annotations which do not require explicit import.

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``annotations`` (default: ``['java.lang.SuppressWarnings', 'java.lang.Deprecated', 'java.lang.annotation.Documented', 'java.lang.annotation.Target', 'java.lang.annotation.Retention', 'java.lang.annotation.Repeatable', 'java.lang.annotation.Inherited']``)

  List of fully qualified annotation classes which are forbidden.

#### Noncompliant Code:

```kotlin
@SuppressWarnings("unused")
class SomeClass()
```

#### Compliant Code:

```kotlin
@Suppress("unused")
class SomeClass()
```

### ForbiddenComment

This rule allows to set a list of comments which are forbidden in the codebase and should only be used during
development. Offending code comments will then be reported.

The regular expressions in `comments` list will have the following behaviors while matching the comments:
* **Each comment will be handled individually.**
* single line comments are always separate, consecutive lines are not merged.
* multi line comments are not split up, the regex will be applied to the whole comment.
* KDoc comments are not split up, the regex will be applied to the whole comment.
* **The following comment delimiters (and indentation before them) are removed** before applying the regex:
`//`, `// `, `/​*`, `/​* `, `/​**`, `*` aligners, `*​/`, ` *​/`
* **The regex is applied as a multiline regex**,
see [Anchors](https://www.regular-expressions.info/anchors.html) for more info.
To match the start and end of each line, use `^` and `$`.
To match the start and end of the whole comment, use `\A` and `\Z`.
To turn off multiline, use `(?-m)` at the start of your regex.
* **The regex is applied with dotall semantics**, meaning `.` will match any character including newlines,
this is to ensure that freeform line-wrapping doesn't mess with simple regexes.
To turn off this behavior, use `(?-s)` at the start of your regex, or use `[^\r\n]*` instead of `.*`.
* **The regex will be searched using "contains" semantics** not "matches",
so partial comment matches will flag forbidden comments.
In practice this means there's no need to start and end the regex with `.*`.

The rule can be configured to add extra comments to the list of forbidden comments, here are some examples:
```yaml
ForbiddenComment:
  comments:
    # Repeat the default configuration if it's still needed.
    - reason: 'Forbidden FIXME todo marker in comment, please fix the problem.'
      value: 'FIXME:'
    - reason: 'Forbidden STOPSHIP todo marker in comment, please address the problem before shipping the code.'
      value: 'STOPSHIP:'
    - reason: 'Forbidden TODO todo marker in comment, please do the changes.'
      value: 'TODO:'
    # Add additional patterns to the list.

    - reason: 'Authors are not recorded in KDoc.'
      value: '@author'

    - reason: 'REVIEW markers are not allowed in production code, only use before PR is merged.'
      value: '^\s*(?i)REVIEW\b'
      # Non-compliant: // REVIEW this code before merging.
      # Compliant: // Preview will show up here.

    - reason: 'Use @androidx.annotation.VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) instead.'
      value: '^private$'
      # Non-compliant: /*private*/fun f() { }

      - reason: 'KDoc tag should have a value.'
        value: '^\s*@(?!suppress|hide)\w+\s*$'
        # Non-compliant: /** ... @see */
    # Compliant: /** ... @throws IOException when there's a network problem */

    - reason: 'include an issue link at the beginning preceded by a space'
      value: 'BUG:(?! https://github\.com/company/repo/issues/\d+).*'
```

By default the commonly used todo markers are forbidden: `TODO:`, `FIXME:` and `STOPSHIP:`.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``comments`` (default: ``['FIXME:', 'STOPSHIP:', 'TODO:']``)

  forbidden comment string patterns

* ``allowedPatterns`` (default: ``''``)

  ignores comments which match the specified regular expression. For example `Ticket|Task`.

#### Noncompliant Code:

```kotlin
val a = "" // TODO: remove please
/**
* FIXME: this is a hack
*/
fun foo() { }
/* STOPSHIP: */
```

### ForbiddenImport

Reports all imports that are forbidden.

This rule allows to set a list of forbidden [forbiddenImports].
This can be used to discourage the use of unstable, experimental or deprecated APIs.

**Active by default**: No

#### Configuration options:

* ``forbiddenImports`` (default: ``[]``)

  List of imports, specified as glob patterns, that are forbidden. It is recommended to also specify a reason.

* ``allowedImports`` (default: ``[]``)

  List of imports, specified as glob patterns, to explicitly allow. Use this to specify exceptions to the forbidden imports.

#### Noncompliant Code:

```kotlin
import kotlin.jvm.JvmField
import kotlin.SinceKotlin
```

### ForbiddenMethodCall

Reports all method or constructor invocations that are forbidden.

This rule allows to set a list of forbidden [methods] or constructors. This can be used to discourage the use
of unstable, experimental or deprecated methods, especially for methods imported from external libraries.

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``methods`` (default: ``['kotlin.io.print', 'kotlin.io.println', 'java.math.BigDecimal.<init>(kotlin.Double)', 'java.math.BigDecimal.<init>(kotlin.String)', 'kotlin.system.measureTimeMillis']``)

  List of fully qualified method signatures which are forbidden. Methods can be defined without full signature (i.e. `java.time.LocalDate.now`) which will report calls of all methods with this name or with full signature (i.e. `java.time.LocalDate(java.time.Clock)`) which would report only call with this concrete signature. If you want to forbid an extension function like `fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: `hello(kotlin.String, kotlin.Int)`. To forbid constructor calls you need to define them with `<init>`, for example `java.util.Date.<init>`. To forbid calls involving type parameters, omit them, for example `fun hello(args: Array<Any>)` is referred to as simply `hello(kotlin.Array)`. To forbid calls involving varargs for example `fun hello(vararg args: String)` you need to define it like `hello(vararg String)`. To forbid methods from the companion object reference the Companion class, for example as `TestClass.Companion.hello()` (even if it is marked `@JvmStatic`). To match function type parameters like `initializer` in `fun <T> lazy(initializer: () -> T)`, use `kotlin.Function{N}` where N is the number of parameters the function takes. For example, `kotlin.lazy(kotlin.Function0)` matches calls to `lazy { ... }`.

#### Noncompliant Code:

```kotlin
fun main() {
println()
val myPrintln : () -> Unit = ::println
kotlin.io.print("Hello, World!")
}
```

### ForbiddenNamedParam

Reports all usages of method, or constructor calls for which using named parameters are forbidden.
This rule can be useful for some method/constructor where using the parameter
name doesn't give add any value to it and only makes the code more verbose

This rule allows to set a list of forbidden [methods] or constructors. These can be used to
discourage the use named parameters.

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``methods`` (default: ``[]``)

  List of fully qualified method signatures for which are named param is forbidden. Methods can be defined without full signature (i.e. `java.time.LocalDate.now`) which will report calls of all methods with this name or with full signature (i.e. `java.time.LocalDate(java.time.Clock)`) which would report only call with this concrete signature. If you want to add an extension function like `fun String.hello(a: Int)` you should add the receiver parameter as the first parameter like this: `hello(kotlin.String, kotlin.Int)`. To add constructor calls you need to define them with `<init>`, for example `java.util.Date.<init>`. To add calls involving type parameters, omit them, for example `fun hello(args: Array<Any>)` is referred to as simply `hello(kotlin.Array)`. To add calls involving varargs for example `fun hello(vararg args: String)` you need to define it like `hello(vararg String)`. To add methods from the companion object reference the Companion class, for example as `TestClass.Companion.hello()` (even if it is marked `@JvmStatic`).

#### Noncompliant Code:

```kotlin
fun foo() {
// `id =` here adds no value
painterResource(id = R.drawable.ic_close)
}
```

#### Compliant Code:

```kotlin
fun foo() {
painterResource(R.drawable.ic_close)
}
```

### ForbiddenOptIn

This rule allows to set a list of forbidden opt-ins. This can be used to avoid opting into an api by accident.
By default, the list of forbidden opt-ins is empty.

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``markerClasses`` (default: ``[]``)

  List of marker classes that are forbidden to be used.

### ForbiddenSuppress

Report suppressions of all forbidden rules.

This rule allows to set a list of [rules] whose suppression is forbidden.
This can be used to discourage the abuse of the `Suppress` and `SuppressWarnings` annotations.

This rule is special in that it itself cannot be suppressed. This ensures that all rules can be enforced strictly with this rule. This rule is not capable of reporting suppression of itself, as that's a language feature with precedence. However, attempting to suppress this rule by any means will have no effect besides producing a warning.

**Active by default**: No

#### Configuration options:

* ``rules`` (default: ``[]``)

  Rules whose suppression is forbidden.

#### Noncompliant Code:

```kotlin
package foo

// When the rule "MaximumLineLength" is forbidden
@Suppress("MaximumLineLength", "UNCHECKED_CAST")
class Bar
```

#### Compliant Code:

```kotlin
package foo

// When the rule "MaximumLineLength" is forbidden
@Suppress("UNCHECKED_CAST")
class Bar
```

### ForbiddenVoid

This rule detects usages of `Void` and reports them as forbidden.
The Kotlin type `Unit` should be used instead. This type corresponds to the `Void` class in Java
and has only one value - the `Unit` object.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Configuration options:

* ``ignoreOverridden`` (default: ``false``)

  ignores void types in signatures of overridden functions

* ``ignoreUsageInGenerics`` (default: ``false``)

  ignore void types as generic arguments

#### Noncompliant Code:

```kotlin
runnable: () -> Void
var aVoid: Void? = null
```

#### Compliant Code:

```kotlin
runnable: () -> Unit
Void::class
```

### FunctionOnlyReturningConstant

A function that only returns a single constant can be misleading. Instead, prefer declaring the constant
as a `const val`.

**Active by default**: Yes - Since v1.2.0

#### Configuration options:

* ``ignoreOverridableFunction`` (default: ``true``)

  if overridden functions should be ignored

* ``ignoreActualFunction`` (default: ``true``)

  if actual functions should be ignored

* ``excludedFunctions`` (default: ``[]``)

  excluded functions

#### Noncompliant Code:

```kotlin
fun functionReturningConstantString() = "1"
```

#### Compliant Code:

```kotlin
const val constantString = "1"
```

### LoopWithTooManyJumpStatements

Loops which contain multiple `break` or `continue` statements are hard to read and understand.
To increase readability they should be refactored into simpler loops.

**Active by default**: Yes - Since v1.2.0

#### Configuration options:

* ``maxJumpCount`` (default: ``1``)

  maximum allowed jumps in a loop

#### Noncompliant Code:

```kotlin
val strs = listOf("foo, bar")
for (str in strs) {
if (str == "bar") {
break
} else {
continue
}
}
```

### MagicNumber

This rule detects and reports usages of magic numbers in the code. Prefer defining constants with clear names
describing what the magic number means.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``ignoreNumbers`` (default: ``['-1', '0', '1', '2']``)

  numbers which do not count as magic numbers

* ``ignoreHashCodeFunction`` (default: ``true``)

  whether magic numbers in hashCode functions should be ignored

* ``ignorePropertyDeclaration`` (default: ``false``)

  whether magic numbers in property declarations should be ignored

* ``ignoreLocalVariableDeclaration`` (default: ``false``)

  whether magic numbers in local variable declarations should be ignored

* ``ignoreConstantDeclaration`` (default: ``true``)

  whether magic numbers in constant declarations should be ignored

* ``ignoreCompanionObjectPropertyDeclaration`` (default: ``true``)

  whether magic numbers in companion object declarations should be ignored

* ``ignoreAnnotation`` (default: ``false``)

  whether magic numbers in annotations should be ignored

* ``ignoreNamedArgument`` (default: ``true``)

  whether magic numbers in named arguments should be ignored

* ``ignoreEnums`` (default: ``false``)

  whether magic numbers in enums should be ignored

* ``ignoreRanges`` (default: ``false``)

  whether magic numbers in ranges should be ignored

* ``ignoreExtensionFunctions`` (default: ``true``)

  whether magic numbers as subject of an extension function should be ignored

#### Noncompliant Code:

```kotlin
class User {

    fun checkName(name: String) {
        if (name.length > 42) {
            throw IllegalArgumentException("username is too long")
        }
        // ...
    }
}
```

#### Compliant Code:

```kotlin
class User {

    fun checkName(name: String) {
        if (name.length > MAX_USERNAME_SIZE) {
            throw IllegalArgumentException("username is too long")
        }
        // ...
    }

    companion object {
        private const val MAX_USERNAME_SIZE = 42
    }
}
```

### MandatoryBracesLoops

This rule detects multi-line `for` and `while` loops which do not have braces.
Adding braces would improve readability and avoid possible errors.

**Active by default**: No

#### Noncompliant Code:

```kotlin
for (i in 0..10)
println(i)

while (true)
println("Hello, world")

do
println("Hello, world")
while (true)
```

#### Compliant Code:

```kotlin
for (i in 0..10) {
println(i)
}

for (i in 0..10) println(i)

while (true) {
println("Hello, world")
}

while (true) println("Hello, world")

do {
println("Hello, world")
} while (true)

do println("Hello, world") while (true)
```

### MaxChainedCallsOnSameLine

Limits the number of chained calls which can be placed on a single line.

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``maxChainedCalls`` (default: ``5``)

  maximum chained calls allowed on a single line

#### Noncompliant Code:

```kotlin
a().b().c().d().e().f()
```

#### Compliant Code:

```kotlin
a().b().c()
.d().e().f()
```

### MaxLineLength

This rule reports lines of code which exceed a defined maximum line length.

Long lines might be hard to read on smaller screens or printouts. Additionally, having a maximum line length
in the codebase will help make the code more uniform.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``maxLineLength`` (default: ``120``)

  maximum line length

* ``excludePackageStatements`` (default: ``true``)

  if package statements should be ignored

* ``excludeImportStatements`` (default: ``true``)

  if import statements should be ignored

* ``excludeCommentStatements`` (default: ``false``)

  if comment statements should be ignored

* ``excludeRawStrings`` (default: ``true``)

  if raw strings should be ignored

### MayBeConstant

This rule identifies and reports properties (`val`) that may be `const val` instead.
Using `const val` can lead to better performance of the resulting bytecode as well as better interoperability with
Java.

**Active by default**: Yes - Since v1.2.0

#### Noncompliant Code:

```kotlin
val myConstant = "abc"
```

#### Compliant Code:

```kotlin
const val MY_CONSTANT = "abc"
```

### ModifierOrder

This rule reports cases in the code where modifiers are not in the correct order. The default modifier order is
taken from: [Modifiers order](https://kotlinlang.org/docs/coding-conventions.html#modifiers-order)

**Active by default**: Yes - Since v1.0.0

#### Noncompliant Code:

```kotlin
lateinit internal val str: String
```

#### Compliant Code:

```kotlin
internal lateinit val str: String
```

### MultilineLambdaItParameter

Lambda expressions are very useful in a lot of cases, and they often include very small chunks of
code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
to make code more concise. However, when you are dealing with lambdas that contain multiple statements,
you might end up with code that is hard to read if you don't specify a readable, descriptive parameter name
explicitly.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
val digits = 1234.let {
println(it)
listOf(it)
}

val digits = 1234.let { it ->
println(it)
listOf(it)
}

val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it ->
println(it)
it + index
}
```

#### Compliant Code:

```kotlin
val digits = 1234.let { explicitParameterName ->
println(explicitParameterName)
listOf(explicitParameterName)
}

val lambda = { item: Int, that: String ->
println(item)
item.toString() + that
}

val digits = 1234.let { listOf(it) }
val digits = 1234.let {
listOf(it)
}
val digits = 1234.let { it -> listOf(it) }
val digits = 1234.let { it ->
listOf(it)
}
val digits = 1234.let { explicit -> listOf(explicit) }
val digits = 1234.let { explicit ->
listOf(explicit)
}
```

### MultilineRawStringIndentation

This rule ensures that raw strings have a consistent indentation.

The content of a multi line raw string should have the same indentation as the enclosing expression plus the
configured indentSize. The closing triple-quotes (`"""`)  must have the same indentation as the enclosing expression.

Warning: Rule `MultilineRawStringIndentation` overlaps with `StringTemplateIndent` from the ktlint rule set.

**Active by default**: No

#### Configuration options:

* ``indentSize`` (default: ``4``)

  indentation size

* ``trimmingMethods`` (default: ``['trimIndent', 'trimMargin']``)

  allows to provide a list of multiline string trimming methods

#### Noncompliant Code:

```kotlin
val a = """
Hello World!
How are you?
""".trimMargin()

val a = """
Hello World!
How are you?
""".trimMargin()
```

#### Compliant Code:

```kotlin
val a = """
Hello World!
How are you?
""".trimMargin()

val a = """
Hello World!
How are you?
""".trimMargin()
```

### NestedClassesVisibility

Nested classes inherit their visibility from the parent class
and are often used to implement functionality local to the class it is nested in.
These nested classes can't have a higher visibility than their parent.
However, the visibility can be further restricted by using a private modifier for instance.
In internal classes the _explicit_ public modifier for nested classes is misleading and thus unnecessary,
because the nested class still has an internal visibility.

**Active by default**: Yes - Since v1.16.0

#### Noncompliant Code:

```kotlin
internal class Outer {
// explicit public modifier still results in an internal nested class
public class Nested
}
```

#### Compliant Code:

```kotlin
internal class Outer {
class Nested1
internal class Nested2
}
```

### NewLineAtEndOfFile

This rule reports files which do not end with a line separator.

**Active by default**: Yes - Since v1.0.0

### NoTabs

This rule reports if tabs are used in Kotlin files.
According to
[Google's Kotlin style guide](https://android.github.io/kotlin-guides/style.html#whitespace-characters)
the only whitespace chars that are allowed in a source file are the line terminator sequence
and the ASCII horizontal space character (0x20). Strings containing tabs are allowed.

**Active by default**: No

### NullableBooleanCheck

Detects nullable boolean checks which use an elvis expression `?:` rather than equals `==`.

Per the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html#nullable-boolean-values-in-conditions)
converting a nullable boolean property to non-null should be done via `!= false` or `== true`
rather than `?: true` or `?: false` (respectively).

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
value ?: true
value ?: false
```

#### Compliant Code:

```kotlin
value != false
value == true
```

### ObjectLiteralToLambda

An anonymous object that does nothing other than the implementation of a single method
can be used as a lambda.

See [SAM conversions](https://kotlinlang.org/docs/java-interop.html#sam-conversions),
[Functional (SAM) interfaces](https://kotlinlang.org/docs/fun-interfaces.html)

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
object : Foo {
override fun bar() {
}
}
```

#### Compliant Code:

```kotlin
Foo {
}
```

### OptionalAbstractKeyword

This rule reports `abstract` modifiers which are unnecessary and can be removed.

**Active by default**: Yes - Since v1.0.0

#### Noncompliant Code:

```kotlin
abstract interface Foo { // abstract keyword not needed

    abstract fun x() // abstract keyword not needed
    abstract var y: Int // abstract keyword not needed
}
```

#### Compliant Code:

```kotlin
interface Foo {

    fun x()
    var y: Int
}
```

### OptionalUnit

It is not necessary to define a return type of `Unit` on functions or to specify a lone Unit statement.
This rule detects and reports instances where the `Unit` return type is specified on functions and the occurrences
of a lone Unit statement.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun foo(): Unit {
return Unit
}
fun foo() = Unit

fun doesNothing() {
Unit
}
```

#### Compliant Code:

```kotlin
fun foo() { }

// overridden no-op functions are allowed
override fun foo() = Unit
```

### ProtectedMemberInFinalClass

Kotlin classes are `final` by default. Thus classes which are not marked as `open` should not contain any `protected`
members. Consider using `private` or `internal` modifiers instead.

**Active by default**: Yes - Since v1.2.0

#### Noncompliant Code:

```kotlin
class ProtectedMemberInFinalClass {
protected var i = 0
}
```

#### Compliant Code:

```kotlin
class ProtectedMemberInFinalClass {
private var i = 0
}
```

### RangeUntilInsteadOfRangeTo

Reports calls to `..` operator instead of calls to `..<`.
`..<` is applicable in cases where the upper range value is described as
open ended range(or in case of integral types some value subtracted by 1).
`..<` helps to prevent off-by-one errors.

**Active by default**: No

#### Noncompliant Code:

```kotlin
for (i in 0..10 - 1) {}
val range = 0..10 - 1
```

#### Compliant Code:

```kotlin
for (i in 0..<10) {}
val range = 0..<10
```

### RedundantConstructorKeyword

This rule checks for redundant constructor keywords.

**Active by default**: No

#### Noncompliant Code:

```kotlin
data class Foo constructor(val foo: Int)
```

#### Compliant Code:

```kotlin
data class Foo(val foo: Int)

data class Bar private constructor(val bar: String) {
constructor(bar: Int): this("$foo")
}
```

### RedundantExplicitType

Local properties do not need their type to be explicitly provided when the inferred type matches the explicit type.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun function() {
val x: String = "string"
}
```

#### Compliant Code:

```kotlin
fun function() {
val x = "string"
}
```

### RedundantHigherOrderMapUsage

Redundant maps add complexity to the code and accomplish nothing. They should be removed or replaced with the proper
operator.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun foo(list: List<Int>): List<Int> {
return list
.filter { it > 5 }
.map { it }
}

fun bar(list: List<Int>): List<Int> {
return list
.filter { it > 5 }
.map {
doSomething(it)
it
}
}

fun baz(set: Set<Int>): List<Int> {
return set.map { it }
}
```

#### Compliant Code:

```kotlin
fun foo(list: List<Int>): List<Int> {
return list
.filter { it > 5 }
}

fun bar(list: List<Int>): List<Int> {
return list
.filter { it > 5 }
.onEach {
doSomething(it)
}
}

fun baz(set: Set<Int>): List<Int> {
return set.toList()
}
```

### RedundantVisibilityModifier

This rule checks for redundant visibility modifiers.
One exemption is the
[explicit API mode](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
In this mode, the visibility modifier should be defined explicitly even if it is public.
Hence, the rule ignores the visibility modifiers in explicit API mode.

**Active by default**: No

#### Noncompliant Code:

```kotlin
public interface Foo { // public per default

    public fun bar() // public per default
}
```

#### Compliant Code:

```kotlin
interface Foo {

    fun bar()
}
```

### ReturnCount

Restrict the number of returns allowed in methods.

Having many exit points in a function can be confusing and impacts readability of the
code.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``max`` (default: ``2``)

  define the maximum number of return statements allowed per function

* ``excludedFunctions`` (default: ``['equals']``)

  define a list of function names to be ignored by this check

* ``excludeLabeled`` (default: ``false``)

  if labeled return statements should be ignored

* ``excludeReturnFromLambda`` (default: ``true``)

  if labeled return from a lambda should be ignored

* ``excludeGuardClauses`` (default: ``false``)

  if true guard clauses at the beginning of a method should be ignored

#### Noncompliant Code:

```kotlin
fun foo(i: Int): String {
when (i) {
1 -> return "one"
2 -> return "two"
else -> return "other"
}
}
```

#### Compliant Code:

```kotlin
fun foo(i: Int): String {
return when (i) {
1 -> "one"
2 -> "two"
else -> "other"
}
}
```

### SafeCast

This rule inspects casts and reports casts which could be replaced with safe casts instead.

**Active by default**: Yes - Since v1.0.0

#### Noncompliant Code:

```kotlin
fun numberMagic(number: Number) {
val i = if (number is Int) number else null
// ...
}
```

#### Compliant Code:

```kotlin
fun numberMagic(number: Number) {
val i = number as? Int
// ...
}
```

### SerialVersionUIDInSerializableClass

Classes which implement the `Serializable` interface should also correctly declare a `serialVersionUID`.
This rule verifies that a `serialVersionUID` was correctly defined and declared as `private`.

[More about `SerialVersionUID`](https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html)

**Active by default**: Yes - Since v1.16.0

#### Noncompliant Code:

```kotlin
class IncorrectSerializable : Serializable {

    companion object {
        val serialVersionUID = 1 // wrong declaration for UID
    }
}
```

#### Compliant Code:

```kotlin
class CorrectSerializable : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}
```

### SpacingAfterPackageDeclaration

This rule verifies spacing between package and import statements as well as between import statements and class
declarations.

**Active by default**: No

#### Noncompliant Code:

```kotlin
package foo
import a.b
class Bar { }
```

#### Compliant Code:

```kotlin
package foo

import a.b

class Bar { }
```

### StringShouldBeRawString

This rule reports when the string can be converted to Kotlin raw string.
Usage of a raw string is preferred as that avoids the need for escaping strings escape characters like \n, \t, ".
Raw string also allows us to represent multiline string without the need of \n.
Also, see [Kotlin coding convention](https://kotlinlang.org/docs/coding-conventions.html#strings)  for
recommendation on using multiline strings

**Active by default**: No

#### Configuration options:

* ``maxEscapedCharacterCount`` (default: ``2``)

  maximum escape characters allowed

* ``ignoredCharacters`` (default: ``[]``)

  list of characters to ignore

#### Noncompliant Code:

```kotlin
val windowJson = "{\n" +
"  \"window\": {\n" +
"    \"title\": \"Sample Quantum With AI and ML Widget\",\n" +
"    \"name\": \"main_window\",\n" +
"    \"width\": 500,\n" +
"    \"height\": 500\n" +
"  }\n" +
"}"

val patRegex = "/^(\\/[^\\/]+){0,2}\\/?\$/gm\n"
```

#### Compliant Code:

```kotlin
val windowJson = """
{
"window": {
"title": "Sample Quantum With AI and ML Widget",
"name": "main_window",
"width": 500,
"height": 500
}
}
""".trimIndent()

val patRegex = """/^(\/[^\/]+){0,2}\/?$/gm"""
```

### ThrowsCount

Functions should have clear `throw` statements. Functions with many `throw` statements can be harder to read and lead
to confusion. Instead, prefer limiting the number of `throw` statements in a function.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``max`` (default: ``2``)

  maximum amount of throw statements in a method

* ``excludeGuardClauses`` (default: ``false``)

  if set to true, guard clauses do not count towards the allowed throws count

#### Noncompliant Code:

```kotlin
fun foo(i: Int) {
when (i) {
1 -> throw IllegalArgumentException()
2 -> throw IllegalArgumentException()
3 -> throw IllegalArgumentException()
}
}
```

#### Compliant Code:

```kotlin
fun foo(i: Int) {
when (i) {
1,2,3 -> throw IllegalArgumentException()
}
}
```

### TrailingWhitespace

This rule reports lines that end with a whitespace.

Note: in KDoc comments we use Markdown, so two spaces at the end of lines should be allowed.
However, JetBrains haven't implemented this in their flavour of "standard" Markdown yet
([in Dokka](https://github.com/Kotlin/dokka/issues/2823),
nor [in KTIJ](https://youtrack.jetbrains.com/issue/KTIJ-6702/KDoc-Dokka-allow-for-newlines-line-breaks-inside-paragraphs)),
which means Markdown line-breaks in KDoc are really only trailing whitespace for now.

**Active by default**: No

### TrimMultilineRawString

All the Raw strings that have more than one line should be followed by `trimMargin()` or `trimIndent()`.

**Active by default**: No

#### Configuration options:

* ``trimmingMethods`` (default: ``['trimIndent', 'trimMargin']``)

  allows to provide a list of multiline string trimming methods

#### Noncompliant Code:

```kotlin
"""
Hello World!
How are you?
"""
```

#### Compliant Code:

```kotlin
"""
|  Hello World!
|  How are you?
""".trimMargin()

"""
Hello World!
How are you?
""".trimIndent()

"""Hello World! How are you?"""
```

### UnderscoresInNumericLiterals

This rule detects and reports long base 10 numbers which should be separated with underscores
for readability. For `Serializable` classes or objects, the field `serialVersionUID` is
explicitly ignored. For floats and doubles, anything to the right of the decimal point is ignored.

**Active by default**: No

#### Configuration options:

* ``acceptableLength`` (default: ``4``)

  Maximum number of consecutive digits that a numeric literal can have without using an underscore

* ``allowNonStandardGrouping`` (default: ``false``)

  If set to false, groups of exactly three digits must be used. If set to true, 100_00 is allowed.

#### Noncompliant Code:

```kotlin
const val DEFAULT_AMOUNT = 1000000
```

#### Compliant Code:

```kotlin
const val DEFAULT_AMOUNT = 1_000_000
```

### UnnecessaryAny

Turn on this rule to flag usage of `any` which can either be replaced with simple `contains` call
or can removed entirely to reduce visual complexity.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
val a = 1
list.any { it == a }
```

#### Compliant Code:

```kotlin
val a = 1
list.contains(a)
```

### UnnecessaryApply

`apply` expressions are used frequently, but sometimes their usage should be replaced with
an ordinary method/extension function call to reduce visual complexity

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
config.apply { version = "1.2" } // can be replaced with `config.version = "1.2"`
config?.apply { environment = "test" } // can be replaced with `config?.environment = "test"`
config?.apply { println(version) } // `apply` can be replaced by `let`
```

#### Compliant Code:

```kotlin
config.apply {
version = "1.2"
environment = "test"
}
```

### UnnecessaryBackticks

This rule reports unnecessary backticks.

**Active by default**: No

#### Noncompliant Code:

```kotlin
class `HelloWorld`
```

#### Compliant Code:

```kotlin
class HelloWorld
```

### UnnecessaryBracesAroundTrailingLambda

In Kotlin functions the last lambda parameter of a function is a function then a lambda expression passed as the
corresponding argument can be placed outside the parentheses.
see [Passing trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas).
Prefer the usage of trailing lambda.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun test() {
repeat(10, {
println(it)
})
}
```

#### Compliant Code:

```kotlin
fun test() {
repeat(10) {
println(it)
}
}
```

### UnnecessaryFilter

Unnecessary filters add complexity to the code and accomplish nothing. They should be removed.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
val x = listOf(1, 2, 3)
.filter { it > 1 }
.count()

val x = listOf(1, 2, 3)
.filter { it > 1 }
.isEmpty()
```

#### Compliant Code:

```kotlin
val x = listOf(1, 2, 3)
.count { it > 2 }

val x = listOf(1, 2, 3)
.none { it > 1 }
```

### UnnecessaryFullyQualifiedName

This rule reports unnecessary fully qualified class names and function calls.
The fully qualified names can be replaced with imports to make the code more readable.

The rule does not report:
- Import statements
- Package declarations
- String literals
- Nested class references without packages (e.g., Outer.Inner)

See [PMD UnnecessaryFullyQualifiedName](https://pmd.github.io/latest/pmd_rules_java_codestyle.html#unnecessaryfullyqualifiedname)
for a similar rule in the Java ecosystem.

**Active by default**: No

#### Noncompliant Code:

```kotlin
class Foo {
fun bar(): java.util.List<String> {
val date = java.time.LocalDate.now()
return java.util.ArrayList()
}
fun baz() {
kotlin.io.println("Hello")
}
}
```

#### Compliant Code:

```kotlin
import java.time.LocalDate
import java.util.ArrayList
import java.util.List

class Foo {
fun bar(): List<String> {
val date = LocalDate.now()
return ArrayList()
}
fun baz() {
println("Hello")
}
}
```

### UnnecessaryInheritance

This rule reports unnecessary super types. Inheriting from `Any` or `Object` is unnecessary and should simply be
removed.

**Active by default**: Yes - Since v1.2.0

#### Noncompliant Code:

```kotlin
class A : Any()
class B : Object()
```

### UnnecessaryInnerClass

This rule reports unnecessary inner classes. Nested classes that do not access members from the outer class do
not require the `inner` qualifier.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
class A {
val foo = "BAR"

    inner class B {
        val fizz = "BUZZ"

        fun printFizz() {
            println(fizz)
        }
    }
}
```

### UnnecessaryLet

`let` expressions are used extensively in our code for null-checking and chaining functions,
but sometimes their usage should be replaced with an ordinary method/extension function call
to reduce visual complexity.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
a.let { print(it) } // can be replaced with `print(a)`
a.let { it.plus(1) } // can be replaced with `a.plus(1)`
a?.let { it.plus(1) } // can be replaced with `a?.plus(1)`
a?.let { that -> that.plus(1) }?.let { it.plus(1) } // can be replaced with `a?.plus(1)?.plus(1)`
a.let { 1.plus(1) } // can be replaced with `1.plus(1)`
a?.let { 1.plus(1) } // can be replaced with `if (a != null) 1.plus(1)`
```

#### Compliant Code:

```kotlin
a?.let { print(it) }
a?.let { 1.plus(it) } ?.let { msg -> print(msg) }
a?.let { it.plus(it) }
val b = a?.let { 1.plus(1) }
```

### UnnecessaryParentheses

This rule reports unnecessary parentheses around expressions.
These unnecessary parentheses can safely be removed.

Added in v1.0.0.RC4

**Active by default**: No

#### Configuration options:

* ``allowForUnclearPrecedence`` (default: ``false``)

  allow parentheses when not strictly required but precedence may be unclear, such as `(a && b) || c`

#### Noncompliant Code:

```kotlin
val local = (5 + 3)

if ((local == 8)) { }

fun foo() {
function({ input -> println(input) })
}
```

#### Compliant Code:

```kotlin
val local = 5 + 3

if (local == 8) { }

fun foo() {
function { input -> println(input) }
}
```

### UnnecessaryReversed

If a sort operation followed by a reverse operation or vise versa should be avoided, and both statements
should be replaced by single equivalent sort operation.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
listOf(1,2)
.sorted()
.asReversed()
```

#### Compliant Code:

```kotlin
listOf(1,2)
.sortedDescending()
```

### UnusedImport

This rule reports unused imports. Unused imports are dead code and should be removed.
Exempt from this rule are imports resulting from references to elements within KDoc and
from destructuring declarations (componentN imports).

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``additionalOperatorSet`` (default: ``[]``)

  Additional operators from libraries or tools, such as 'assign'(e.g. compiler plugins for Gradle).

### UnusedParameter

An unused parameter can be removed to simplify the signature of the function.

**Active by default**: Yes - Since v1.23.0

**Aliases**: UNUSED_PARAMETER, unused

#### Configuration options:

* ``allowedNames`` (default: ``'ignored|expected'``)

  unused parameter names matching this regex are ignored

#### Noncompliant Code:

```kotlin
fun foo(unused: String) {
println()
}
```

#### Compliant Code:

```kotlin
fun foo(used: String) {
println(used)
}
```

### UnusedPrivateClass

Reports unused private classes. If private classes are unused they should be removed. Otherwise, this dead code
can lead to confusion and potential bugs.

**Active by default**: Yes - Since v1.2.0

**Aliases**: unused

### UnusedPrivateFunction

Reports unused private functions.

If these private functions are unused they should be removed. Otherwise, this dead code
can lead to confusion and potential bugs.

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

**Aliases**: unused

#### Configuration options:

* ``allowedNames`` (default: ``''``)

  unused private function names matching this regex are ignored

### UnusedPrivateProperty

An unused private property can be removed to simplify the source file.

This rule also detects unused constructor parameters since these can become
properties of the class when they are declared with `val` or `var`.

**Active by default**: Yes - Since v1.23.0

**Requires Type Resolution**

**Aliases**: unused

#### Configuration options:

* ``allowedNames`` (default: ``'ignored|expected|serialVersionUID'``)

  unused property names matching this regex are ignored

#### Noncompliant Code:

```kotlin
class Foo {
private val unused = "unused"
}
```

#### Compliant Code:

```kotlin
class Foo {
private val used = "used"

    fun greet() {
        println(used)
    }
}
```

### UnusedVariable

An unused variable can be removed to simplify the source file.

**Active by default**: Yes - Since v2.0.0

**Requires Type Resolution**

**Aliases**: UNUSED_VARIABLE, unused

#### Configuration options:

* ``allowedNames`` (default: ``'ignored|_'``)

  unused variables names matching this regex are ignored

#### Noncompliant Code:

```kotlin
fun foo() {
val unused = "unused"
}
```

#### Compliant Code:

```kotlin
fun foo() {
val used = "used"
println(used)
}
```

### UseAnyOrNoneInsteadOfFind

Turn on this rule to flag `find` calls for null check that can be replaced with a `any` or `none` call.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
listOf(1, 2, 3).find { it == 4 } != null
listOf(1, 2, 3).find { it == 4 } == null
```

#### Compliant Code:

```kotlin
listOf(1, 2, 3).any { it == 4 }
listOf(1, 2, 3).none { it == 4 }
```

### UseArrayLiteralsInAnnotations

This rule detects annotations which use the arrayOf(...) syntax instead of the array literal [...] syntax.
The latter should be preferred as it is more readable.

**Active by default**: Yes - Since v1.21.0

#### Noncompliant Code:

```kotlin
@PositiveCase(arrayOf("..."))
```

#### Compliant Code:

```kotlin
@NegativeCase(["..."])
```

### UseCheckNotNull

Turn on this rule to flag `check` calls for not-null check that can be replaced with a `checkNotNull` call.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
check(x != null)
```

#### Compliant Code:

```kotlin
checkNotNull(x)
```

### UseCheckOrError

Kotlin provides a concise way to check invariants as well as pre- and post-conditions.
Prefer them instead of manually throwing an IllegalStateException.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
if (value == null) throw IllegalStateException("value should not be null")
if (value < 0) throw IllegalStateException("value is $value but should be at least 0")
when(a) {
1 -> doSomething()
else -> throw IllegalStateException("Unexpected value")
}
```

#### Compliant Code:

```kotlin
checkNotNull(value) { "value should not be null" }
check(value >= 0) { "value is $value but should be at least 0" }
when(a) {
1 -> doSomething()
else -> error("Unexpected value")
}
```

### UseDataClass

Classes that simply hold data should be refactored into a `data class`. Data classes are specialized to hold data
and generate `hashCode`, `equals` and `toString` implementations as well.

Read more about [data classes](https://kotlinlang.org/docs/data-classes.html)

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``allowVars`` (default: ``false``)

  allows to relax this rule in order to exclude classes that contains one (or more) vars

#### Noncompliant Code:

```kotlin
class DataClassCandidate(val i: Int) {
val i2: Int = 0
}
```

#### Compliant Code:

```kotlin
data class DataClass(val i: Int, val i2: Int)

// classes with delegating interfaces are compliant
interface I
class B() : I
class A(val b: B) : I by b
```

### UseEmptyCounterpart

Instantiation of an object's "empty" state should use the object's "empty" initializer for clarity purposes.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
arrayOf()
listOf() // or listOfNotNull()
mapOf()
sequenceOf()
setOf()
```

#### Compliant Code:

```kotlin
emptyArray()
emptyList()
emptyMap()
emptySequence()
emptySet()
```

### UseIfEmptyOrIfBlank

This rule detects `isEmpty` or `isBlank` calls to assign a default value. They can be replaced with `ifEmpty` or
`ifBlank` calls.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun test(list: List<Int>, s: String) {
val a = if (list.isEmpty()) listOf(1) else list
val b = if (list.isNotEmpty()) list else listOf(2)
val c = if (s.isBlank()) "foo" else s
val d = if (s.isNotBlank()) s else "bar"
}
```

#### Compliant Code:

```kotlin
fun test(list: List<Int>, s: String) {
val a = list.ifEmpty { listOf(1) }
val b = list.ifEmpty { listOf(2) }
val c = s.ifBlank { "foo" }
val d = s.ifBlank { "bar" }
}
```

### UseIfInsteadOfWhen

Binary expressions are better expressed using an `if` expression than a `when` expression.

See [if versus when](https://kotlinlang.org/docs/coding-conventions.html#if-versus-when)

**Active by default**: No

#### Configuration options:

* ``ignoreWhenContainingVariableDeclaration`` (default: ``false``)

  ignores when statements with a variable declaration used in the subject

#### Noncompliant Code:

```kotlin
when (x) {
null -> true
else -> false
}
```

#### Compliant Code:

```kotlin
if (x == null) true else false
```

### UseIsNullOrEmpty

This rule detects null or empty checks that can be replaced with `isNullOrEmpty()` call.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun foo(x: List<Int>?) {
if (x == null || x.isEmpty()) return
}
fun bar(x: List<Int>?) {
if (x == null || x.count() == 0) return
}
fun baz(x: List<Int>?) {
if (x == null || x.size == 0) return
}
```

#### Compliant Code:

```kotlin
if (x.isNullOrEmpty()) return
```

### UseLet

`if` expressions that either check for not-null and return `null` in the false case or check for `null` and returns
`null` in the truthy case are better represented as `?.let {}` blocks.

**Active by default**: No

#### Noncompliant Code:

```kotlin
if (x != null) { transform(x) } else null
if (x == null) null else y
```

#### Compliant Code:

```kotlin
x?.let { transform(it) }
x?.let { y }
```

### UseOrEmpty

This rule detects `?: emptyList()` that can be replaced with `orEmpty()` call.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun test(x: List<Int>?, s: String?) {
val a = x ?: emptyList()
val b = s ?: ""
}
```

#### Compliant Code:

```kotlin
fun test(x: List<Int>?, s: String?) {
val a = x.orEmpty()
val b = s.orEmpty()
}
```

### UseRequire

Kotlin provides a much more concise way to check preconditions than to manually throw an
IllegalArgumentException.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
if (value == null) throw IllegalArgumentException("value should not be null")
if (value < 0) throw IllegalArgumentException("value is $value but should be at least 0")
```

#### Compliant Code:

```kotlin
requireNotNull(value) { "value should not be null" }
require(value >= 0) { "value is $value but should be at least 0" }
```

### UseRequireNotNull

Turn on this rule to flag `require` calls for not-null check that can be replaced with a `requireNotNull` call.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
require(x != null)
```

#### Compliant Code:

```kotlin
requireNotNull(x)
```

### UseSumOfInsteadOfFlatMapSize

Turn on this rule to flag `flatMap` and `size/count` calls that can be replaced with a `sumOf` call.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
class Foo(val foo: List<Int>)
list.flatMap { it.foo }.size
list.flatMap { it.foo }.count()
list.flatMap { it.foo }.count { it > 2 }
listOf(listOf(1), listOf(2, 3)).flatten().size
```

#### Compliant Code:

```kotlin
list.sumOf { it.foo.size }
list.sumOf { it.foo.count() }
list.sumOf { it.foo.count { foo -> foo > 2 } }
listOf(listOf(1), listOf(2, 3)).sumOf { it.size }
```

### UselessCallOnNotNull

The Kotlin stdlib provides some functions that are designed to operate on references that may be null. These
functions can also be called on non-nullable references or on collections or sequences that are known to be empty -
the calls are redundant in this case and can be removed or should be changed to a call that does not check whether
the value is null or not.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
val testList = listOf("string").orEmpty()
val testList2 = listOf("string").orEmpty().map { _ }
val testList3 = listOfNotNull("string")
val testString = ""?.isNullOrBlank()
```

#### Compliant Code:

```kotlin
val testList = listOf("string")
val testList2 = listOf("string").map { }
val testList3 = listOf("string")
val testString = ""?.isBlank()
```

### UtilityClassWithPublicConstructor

A class which only contains utility variables and functions with no concrete implementation can be refactored
into an `object` or a class with a non-public constructor.
Furthermore, this rule reports utility classes which are not final.

**Active by default**: Yes - Since v1.2.0

#### Noncompliant Code:

```kotlin
class UtilityClassViolation {

    // public constructor here
    constructor() {
        // ...
    }

    companion object {
        val i = 0
    }
}

open class UtilityClassViolation private constructor() {

    // ...
}
```

#### Compliant Code:

```kotlin
class UtilityClass {

    private constructor() {
        // ...
    }

    companion object {
        val i = 0
    }
}
object UtilityClass {

    val i = 0
}
```

### VarCouldBeVal

Reports var declarations (both local variables and private class properties) that could be val,
as they are not re-assigned. Val declarations are assign-once (read-only), which makes understanding
the current state easier.

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

**Aliases**: CanBeVal

#### Configuration options:

* ``ignoreLateinitVar`` (default: ``false``)

  Whether to ignore uninitialized lateinit vars

#### Noncompliant Code:

```kotlin
fun example() {
var i = 1 // violation: this variable is never re-assigned
val j = i + 1
}
```

#### Compliant Code:

```kotlin
fun example() {
val i = 1
val j = i + 1
}
```

### WildcardImport

Wildcard imports should be replaced with imports using fully qualified class names. This helps increase clarity of
which classes are imported and helps prevent naming conflicts.

Library updates can introduce naming clashes with your own classes which might result in compilation errors.

**NOTE**: This rule has a twin implementation NoWildcardImports in the ktlint rule set.
When suppressing an issue of WildcardImport in the baseline file, make sure to suppress the corresponding NoWildcardImports issue.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``excludeImports`` (default: ``['java.util.*']``)

  Define a list of package names that should be allowed to be imported with wildcard imports.

#### Noncompliant Code:

```kotlin
import dev.detekt.*

class DetektElements {
val element1 = DetektElement1()
val element2 = DetektElement2()
}
```

#### Compliant Code:

```kotlin
import dev.detekt.DetektElement1
import dev.detekt.DetektElement2

class DetektElements {
val element1 = DetektElement1()
val element2 = DetektElement2()
}
```
