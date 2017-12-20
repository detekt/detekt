# style

The Style ruleset provides rules that assert the style of the code.
This will help keep code in line with the given
code style guidelines.

## Content

1. [CollapsibleIfStatements](#CollapsibleIfStatements)
2. [ReturnCount](#ReturnCount)
3. [ThrowsCount](#ThrowsCount)
4. [NewLineAtEndOfFile](#NewLineAtEndOfFile)
5. [WildcardImport](#WildcardImport)
6. [MaxLineLength](#MaxLineLength)
7. [EqualsNullCall](#EqualsNullCall)
8. [ForbiddenComment](#ForbiddenComment)
9. [ForbiddenImport](#ForbiddenImport)
10. [FunctionOnlyReturningConstant](#FunctionOnlyReturningConstant)
11. [SpacingBetweenPackageAndImports](#SpacingBetweenPackageAndImports)
12. [LoopWithTooManyJumpStatements](#LoopWithTooManyJumpStatements)
13. [MemberNameEqualsClassName](#MemberNameEqualsClassName)
14. [VariableNaming](#VariableNaming)
15. [VariableMinLength](#VariableMinLength)
16. [VariableMaxLength](#VariableMaxLength)
17. [TopLevelPropertyNaming](#TopLevelPropertyNaming)
18. [ObjectPropertyNaming](#ObjectPropertyNaming)
19. [PackageNaming](#PackageNaming)
20. [ClassNaming](#ClassNaming)
21. [EnumNaming](#EnumNaming)
22. [FunctionNaming](#FunctionNaming)
23. [FunctionMaxLength](#FunctionMaxLength)
24. [FunctionMinLength](#FunctionMinLength)
25. [ForbiddenClassName](#ForbiddenClassName)
26. [SafeCast](#SafeCast)
27. [UnnecessaryAbstractClass](#UnnecessaryAbstractClass)
28. [UnnecessaryParentheses](#UnnecessaryParentheses)
29. [UnnecessaryInheritance](#UnnecessaryInheritance)
30. [UtilityClassWithPublicConstructor](#UtilityClassWithPublicConstructor)
31. [OptionalAbstractKeyword](#OptionalAbstractKeyword)
32. [OptionalWhenBraces](#OptionalWhenBraces)
33. [OptionalReturnKeyword](#OptionalReturnKeyword)
34. [OptionalUnit](#OptionalUnit)
35. [ProtectedMemberInFinalClass](#ProtectedMemberInFinalClass)
36. [SerialVersionUIDInSerializableClass](#SerialVersionUIDInSerializableClass)
37. [MagicNumber](#MagicNumber)
38. [ModifierOrder](#ModifierOrder)
39. [DataClassContainsFunctions](#DataClassContainsFunctions)
40. [UseDataClass](#UseDataClass)
41. [UnusedImports](#UnusedImports)
42. [ExpressionBodySyntax](#ExpressionBodySyntax)
43. [NestedClassesVisibility](#NestedClassesVisibility)
44. [RedundantVisibilityModifierRule](#RedundantVisibilityModifierRule)
## Rules in the `style` rule set:

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

### NewLineAtEndOfFile

TODO: Specify description

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

### MaxLineLength

TODO: Specify description

#### Configuration options:

* `maxLineLength` (default: `120`)

   maximum line length

* `excludePackageStatements` (default: `false`)

   if package statements should be ignored

* `excludeImportStatements` (default: `false`)

   if import statements should be ignored

### EqualsNullCall

TODO: Specify description

#### Noncompliant Code:

```kotlin
fun isNull(str: String) {
    str.equals(null)
}
```

#### Compliant Code:

```kotlin
fun isNull(str: String) {
    str == null
}
```

### ForbiddenComment

TODO: Specify description

#### Configuration options:

* `values` (default: `'TODO:,FIXME:,STOPSHIP:'`)

   forbidden comment strings

#### Noncompliant Code:

```kotlin
TODO:,FIXME:,STOPSHIP:
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

### MemberNameEqualsClassName

TODO: Specify description

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

### VariableNaming

TODO: Specify description

#### Configuration options:

* `variablePattern` (default: `'[a-z][A-Za-z0-9]*'`)

   naming pattern (default: '[a

* `privateVariablePattern` (default: `'(_)?[a-z][A-Za-z0-9]*'`)

   naming pattern ?[a

### VariableMinLength

TODO: Specify description

#### Configuration options:

* `minimumVariableNameLength` (default: `3`)

   maximum name length

### VariableMaxLength

TODO: Specify description

#### Configuration options:

* `maximumVariableNameLength` (default: `30`)

   maximum name length

### TopLevelPropertyNaming

TODO: Specify description

#### Configuration options:

* `constantPattern` (default: `'[A-Z][_A-Z0-9]*'`)

   naming pattern (default: '[A

* `propertyPattern` (default: `'[a-z][A-Za-z\d]*'`)

   naming pattern (default: '[a

* `privatePropertyPattern` (default: `'(_)?[a-z][A-Za-z0-9]*'`)

   naming pattern ?[a

### ObjectPropertyNaming

TODO: Specify description

#### Configuration options:

* `propertyPattern` (default: `'[A-Za-z][_A-Za-z0-9]*'`)

   naming pattern (default: '[A

### PackageNaming

TODO: Specify description

#### Configuration options:

* `packagePattern` (default: `'^[a-z]+(\.[a-z][a-z0-9]*)*$'`)

   naming pattern (default: '^[a

### ClassNaming

TODO: Specify description

#### Configuration options:

* `classPattern` (default: `'[A-Z$][a-zA-Z0-9$]*'`)

   naming pattern (default: '[A

### EnumNaming

TODO: Specify description

#### Configuration options:

* `enumEntryPattern` (default: `'^[A-Z$][a-zA-Z_$]*$'`)

   naming pattern (default: '^[A

### FunctionNaming

TODO: Specify description

#### Configuration options:

* `functionPattern` (default: `'^([a-z$][a-zA-Z$0-9]*)|(`.*`)$'`)

   naming pattern (default: '^([a

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

### ForbiddenClassName

TODO: Specify description

#### Configuration options:

* `forbiddenName` (default: `''`)

   forbidden class names

### SafeCast

TODO: Specify description

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

### UnnecessaryInheritance

TODO: Specify description

#### Noncompliant Code:

```kotlin
class A : Any()
class B : Object()
```

### UtilityClassWithPublicConstructor

TODO: Specify description

#### Noncompliant Code:

```kotlin
class UtilityClassWithPublicConstructor {

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

### OptionalWhenBraces

TODO: Specify description

#### Noncompliant Code:

```kotlin
val i = 1
when (1) {
    1 -> { println("one") } // unnecessary curly braces
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

### ProtectedMemberInFinalClass

TODO: Specify description

#### Noncompliant Code:

```kotlin
class ProtectedMemberInFinalClass {
    protected var i = 0
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

### MagicNumber

TODO: Specify description

#### Configuration options:

* `ignoreNumbers` (default: `'-1,0,1,2'`)

   numbers which do not count as magic numbers (default: '

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

#### Compliant Code:

```kotlin
data class DataClassWithFunctions(val i: Int) {
}
```

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

### UnusedImports

TODO: Specify description

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
