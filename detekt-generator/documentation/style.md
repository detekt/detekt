# style

The Style ruleset provides rules that assert the style of the code.
This will help keep code in line with the given
code style guidelines.

## Content

1. [ClassNaming](#classnaming)
2. [CollapsibleIfStatements](#collapsibleifstatements)
3. [DataClassContainsFunctions](#dataclasscontainsfunctions)
4. [EnumNaming](#enumnaming)
5. [EqualsNullCall](#equalsnullcall)
6. [ExpressionBodySyntax](#expressionbodysyntax)
7. [ForbiddenClassName](#forbiddenclassname)
8. [ForbiddenComment](#forbiddencomment)
9. [ForbiddenImport](#forbiddenimport)
10. [FunctionMaxLength](#functionmaxlength)
11. [FunctionMinLength](#functionminlength)
12. [FunctionNaming](#functionnaming)
13. [FunctionOnlyReturningConstant](#functiononlyreturningconstant)
14. [LoopWithTooManyJumpStatements](#loopwithtoomanyjumpstatements)
15. [MagicNumber](#magicnumber)
16. [MatchingDeclarationName](#matchingdeclarationname)
17. [MaxLineLength](#maxlinelength)
18. [MemberNameEqualsClassName](#membernameequalsclassname)
19. [ModifierOrder](#modifierorder)
20. [NestedClassesVisibility](#nestedclassesvisibility)
21. [NewLineAtEndOfFile](#newlineatendoffile)
22. [ObjectPropertyNaming](#objectpropertynaming)
23. [OptionalAbstractKeyword](#optionalabstractkeyword)
24. [OptionalReturnKeyword](#optionalreturnkeyword)
25. [OptionalUnit](#optionalunit)
26. [OptionalWhenBraces](#optionalwhenbraces)
27. [PackageNaming](#packagenaming)
28. [ProtectedMemberInFinalClass](#protectedmemberinfinalclass)
29. [RedundantVisibilityModifierRule](#redundantvisibilitymodifierrule)
30. [ReturnCount](#returncount)
31. [SafeCast](#safecast)
32. [SerialVersionUIDInSerializableClass](#serialversionuidinserializableclass)
33. [SpacingBetweenPackageAndImports](#spacingbetweenpackageandimports)
34. [ThrowsCount](#throwscount)
35. [TopLevelPropertyNaming](#toplevelpropertynaming)
36. [UnnecessaryAbstractClass](#unnecessaryabstractclass)
37. [UnnecessaryInheritance](#unnecessaryinheritance)
38. [UnnecessaryParentheses](#unnecessaryparentheses)
39. [UntilInsteadOfRangeTo](#untilinsteadofrangeto)
40. [UnusedImports](#unusedimports)
41. [UseDataClass](#usedataclass)
42. [UtilityClassWithPublicConstructor](#utilityclasswithpublicconstructor)
43. [VariableMaxLength](#variablemaxlength)
44. [VariableMinLength](#variableminlength)
45. [VariableNaming](#variablenaming)
46. [WildcardImport](#wildcardimport)
## Rules in the `style` rule set:

### ClassNaming

TODO: Specify description

#### Configuration options:

* `classPattern` (default: `'[A-Z$][a-zA-Z0-9$]*'`)

   naming pattern

### CollapsibleIfStatements

TODO: Specify description

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

TODO: Specify description

#### Configuration options:

* `conversionFunctionPrefix` (default: `'to'`)

   allowed conversion function names

#### Noncompliant Code:

```kotlin
data class DataClassWithFunctions(val i: Int) {
    fun foo() { }
}
```

### EnumNaming

TODO: Specify description

#### Configuration options:

* `enumEntryPattern` (default: `'^[A-Z$][a-zA-Z_$]*$'`)

   naming pattern

### EqualsNullCall

TODO: Specify description

#### Noncompliant Code:

```kotlin
fun isNull(str: String) = str.equals(null)
```

#### Compliant Code:

```kotlin
fun isNull(str: String) = str == null
```

### ExpressionBodySyntax

TODO: Specify description

#### Noncompliant Code:

```kotlin
fun stuff(): Int {
    return 5
}
```

#### Compliant Code:

```kotlin
fun stuff() = 5
```

### ForbiddenClassName

TODO: Specify description

#### Configuration options:

* `forbiddenName` (default: `''`)

   forbidden class names

### ForbiddenComment

TODO: Specify description

#### Configuration options:

* `values` (default: `'TODO:,FIXME:,STOPSHIP:'`)

   forbidden comment strings

#### Noncompliant Code:

```kotlin
// TODO:,FIXME:,STOPSHIP:
fun foo() { }
```

### ForbiddenImport

TODO: Specify description

#### Configuration options:

* `imports` (default: `''`)

   imports which should not be used

#### Noncompliant Code:

```kotlin
package foo

import kotlin.jvm.JvmField
import kotlin.SinceKotlin
```

### FunctionMaxLength

TODO: Specify description

#### Configuration options:

* `maximumFunctionNameLength` (default: `30`)

   maximum name length

### FunctionMinLength

TODO: Specify description

#### Configuration options:

* `minimumFunctionNameLength` (default: `3`)

   minimum name length

### FunctionNaming

TODO: Specify description

#### Configuration options:

* `functionPattern` (default: `'^([a-z$][a-zA-Z$0-9]*)|(`.*`)$'`)

   naming pattern

### FunctionOnlyReturningConstant

TODO: Specify description

#### Configuration options:

* `ignoreOverridableFunction` (default: `true`)

   if overriden functions should be ignored

* `excludedFunctions` (default: `'describeContents'`)

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

TODO: Specify description

#### Configuration options:

* `maxJumpCount` (default: `1`)

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

TODO: Specify description

#### Configuration options:

* `ignoreNumbers` (default: `'-1,0,1,2'`)

   numbers which do not count as magic numbers

* `ignoreHashCodeFunction` (default: `false`)

   whether magic numbers in hashCode functions should be ignored

* `ignorePropertyDeclaration` (default: `false`)

   whether magic numbers in property declarations should be ignored

* `ignoreConstantDeclaration` (default: `true`)

   whether magic numbers in property declarations should be ignored

* `ignoreCompanionObjectPropertyDeclaration` (default: `true`)

   whether magic numbers in companion object
declarations should be ignored

* `ignoreAnnotation` (default: `false`)

   whether magic numbers in annotations should be ignored

* `ignoreNamedArgument` (default: `true`)

   whether magic numbers in named arguments should be ignored

* `ignoreEnums` (default: `false`)

   whether magic numbers in enums should be ignored

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

### MatchingDeclarationName

"If a Kotlin file contains a single class (potentially with related top-level declarations),
its name should be the same as the name of the class, with the .kt extension appended.
If a file contains multiple classes, or only top-level declarations,
choose a name describing what the file contains, and name the file accordingly.
Use camel humps with an uppercase first letter (e.g. ProcessDeclarations.kt).

The name of the file should describe what the code in the file does.
Therefore, you should avoid using meaningless words such as "Util" in file names." - Official Kotlin Style Guide

More information at: http://kotlinlang.org/docs/reference/coding-conventions.html

#### Noncompliant Code:

```kotlin
class Foo // FooUtils.kt

fun Bar.toFoo(): Foo = ...
fun Foo.toBar(): Bar = ...
```

#### Compliant Code:

```kotlin
class Foo { // Foo.kt
    fun stuff() = 42
}

fun Bar.toFoo(): Foo = ...
```

### MaxLineLength

TODO: Specify description

#### Configuration options:

* `maxLineLength` (default: `120`)

   maximum line length

* `excludePackageStatements` (default: `false`)

   if package statements should be ignored

* `excludeImportStatements` (default: `false`)

   if import statements should be ignored

### MemberNameEqualsClassName

This rule reports a member that has the same as the containing class or object.
This might result in confusion.
The member should either be renamed or changed to a constructor.
Factory functions that create an instance of the class are exempt from this rule.

#### Configuration options:

* `ignoreOverriddenFunction` (default: `true`)

   if overridden functions should be ignored

#### Noncompliant Code:

```kotlin
class MethodNameEqualsClassName {

    fun methodNameEqualsClassName() { }
}

class PropertyNameEqualsClassName {

    val propertyEqualsClassName = 0
}
```

#### Compliant Code:

```kotlin
class Manager {

    companion object {
        // factory functions can have the same name as the class
        fun manager(): Manager {
            return Manager()
        }
    }
}
```

### ModifierOrder

TODO: Specify description

#### Noncompliant Code:

```kotlin
lateinit internal private val str: String
```

#### Compliant Code:

```kotlin
private internal lateinit val str: String
```

### NestedClassesVisibility

TODO: Specify description

#### Noncompliant Code:

```kotlin
internal class NestedClassesVisibility {

    public class NestedPublicClass // should not be public
}
```

#### Compliant Code:

```kotlin
internal class NestedClassesVisibility {

    internal class NestedPublicClass
}
```

### NewLineAtEndOfFile

TODO: Specify description

### ObjectPropertyNaming

TODO: Specify description

#### Configuration options:

* `propertyPattern` (default: `'[A-Za-z][_A-Za-z0-9]*'`)

   naming pattern

### OptionalAbstractKeyword

TODO: Specify description

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

### OptionalReturnKeyword

TODO: Specify description

#### Noncompliant Code:

```kotlin
val z = if (true) return x else return y
```

#### Compliant Code:

```kotlin
val z = if (true) x else y
```

### OptionalUnit

TODO: Specify description

#### Noncompliant Code:

```kotlin
fun foo(): Unit { }
```

#### Compliant Code:

```kotlin
fun foo() { }
```

### OptionalWhenBraces

TODO: Specify description

#### Noncompliant Code:

```kotlin
val i = 1
when (1) {
    1 -> { println("one") } // unnecessary curly braces since there is only one statement
    else -> println("else")
}
```

#### Compliant Code:

```kotlin
val i = 1
when (1) {
    1 -> println("one")
    else -> println("else")
}
```

### PackageNaming

TODO: Specify description

#### Configuration options:

* `packagePattern` (default: `'^[a-z]+(\.[a-z][a-z0-9]*)*$'`)

   naming pattern

### ProtectedMemberInFinalClass

TODO: Specify description

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

### RedundantVisibilityModifierRule

TODO: Specify description

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

Restrict the number of return methods allowed in methods.

Having many exit points in a function can be confusing and impacts readability of the
code.

#### Configuration options:

* `max` (default: `2`)

   define the maximum number of return statements allowed per function

* `excludedFunctions` (default: `"equals"`)

   define functions to be ignored by this check

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

TODO: Specify description

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

TODO: Specify description

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
        const val serialVersionUID = 1L
    }
}
```

### SpacingBetweenPackageAndImports

TODO: Specify description

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

### ThrowsCount

TODO: Specify description

#### Configuration options:

* `max` (default: `2`)

   maximum amount of throw statements in a method

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

### TopLevelPropertyNaming

TODO: Specify description

#### Configuration options:

* `constantPattern` (default: `'[A-Z][_A-Z0-9]*'`)

   naming pattern

* `propertyPattern` (default: `'[a-z][A-Za-z\d]*'`)

   naming pattern

* `privatePropertyPattern` (default: `'(_)?[a-z][A-Za-z0-9]*'`)

   naming pattern

### UnnecessaryAbstractClass

TODO: Specify description

#### Noncompliant Code:

```kotlin
abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members

    abstract val i: Int
    abstract fun f()
}

abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members

    val i: Int = 0
    fun f() { }
}
```

### UnnecessaryInheritance

TODO: Specify description

#### Noncompliant Code:

```kotlin
class A : Any()
class B : Object()
```

### UnnecessaryParentheses

Reports unnecessary parentheses around expressions.

Added in v1.0.0.RC4

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

### UntilInsteadOfRangeTo

Reports calls to '..' operator instead of calls to 'until'.
'until' is applicable in cases where the upper range value is described as
some value subtracted by 1. 'until' helps to prevent off-by-one errors.

#### Noncompliant Code:

```kotlin
for (i in 0 .. 10 - 1) {}
val range = 0 .. 10 - 1
```

#### Compliant Code:

```kotlin
for (i in 0 until 10) {}
val range = 0 until 10
```

### UnusedImports

TODO: Specify description

### UseDataClass

TODO: Specify description

#### Noncompliant Code:

```kotlin
class DataClassCandidate(val i: Int) {

    val i2: Int = 0
}
```

#### Compliant Code:

```kotlin
data class DataClass(val i: Int, val i2: Int)
```

### UtilityClassWithPublicConstructor

TODO: Specify description

#### Noncompliant Code:

```kotlin
class UtilityClass {

    // public constructor here
    constructor() {
        // ...
    }

    companion object {
        val i = 0
    }
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
```

### VariableMaxLength

TODO: Specify description

#### Configuration options:

* `maximumVariableNameLength` (default: `64`)

   maximum name length

### VariableMinLength

TODO: Specify description

#### Configuration options:

* `minimumVariableNameLength` (default: `1`)

   maximum name length

### VariableNaming

TODO: Specify description

#### Configuration options:

* `variablePattern` (default: `'[a-z][A-Za-z0-9]*'`)

   naming pattern

* `privateVariablePattern` (default: `'(_)?[a-z][A-Za-z0-9]*'`)

   naming pattern

### WildcardImport

Wildcard imports should be replaced with imports using fully qualified class names. This helps increase clarity of
which classes are imported and helps prevent naming conflicts.

Library updates can introduce naming clashes with your own classes which might result in compilation errors.

#### Configuration options:

* `excludeImports` (default: `'java.util.*,kotlinx.android.synthetic.*'`)

   Define a whitelist of package names that should be allowed to be imported
with wildcard imports.

#### Noncompliant Code:

```kotlin
package test

import io.gitlab.arturbosch.detekt.*

class DetektElements {
    val element1 = DetektElement1()
    val element2 = DetektElement2()
}
```

#### Compliant Code:

```kotlin
package test

import io.gitlab.arturbosch.detekt.DetektElement1
import io.gitlab.arturbosch.detekt.DetektElement2

class DetektElements {
    val element1 = DetektElement1()
    val element2 = DetektElement2()
}
```
