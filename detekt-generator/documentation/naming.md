# naming

The naming ruleset contains rules which assert the naming of different parts of the codebase.

## Content

1. [ClassNaming](#classnaming)
2. [EnumNaming](#enumnaming)
3. [ForbiddenClassName](#forbiddenclassname)
4. [FunctionMaxLength](#functionmaxlength)
5. [FunctionMinLength](#functionminlength)
6. [FunctionNaming](#functionnaming)
7. [MatchingDeclarationName](#matchingdeclarationname)
8. [MemberNameEqualsClassName](#membernameequalsclassname)
9. [ObjectPropertyNaming](#objectpropertynaming)
10. [PackageNaming](#packagenaming)
11. [TopLevelPropertyNaming](#toplevelpropertynaming)
12. [VariableMaxLength](#variablemaxlength)
13. [VariableMinLength](#variableminlength)
14. [VariableNaming](#variablenaming)
## Rules in the `naming` rule set:

### ClassNaming

TODO: Specify description

#### Configuration options:

* `classPattern` (default: `'[A-Z$][a-zA-Z0-9$]*'`)

   naming pattern

### EnumNaming

TODO: Specify description

#### Configuration options:

* `enumEntryPattern` (default: `'^[A-Z$][a-zA-Z_$]*$'`)

   naming pattern

### ForbiddenClassName

TODO: Specify description

#### Configuration options:

* `forbiddenName` (default: `''`)

   forbidden class names

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

### MatchingDeclarationName

"If a Kotlin file contains a single non-private class (potentially with related top-level declarations),
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

### ObjectPropertyNaming

TODO: Specify description

#### Configuration options:

* `propertyPattern` (default: `'[A-Za-z][_A-Za-z0-9]*'`)

   naming pattern

### PackageNaming

TODO: Specify description

#### Configuration options:

* `packagePattern` (default: `'^[a-z]+(\.[a-z][a-z0-9]*)*$'`)

   naming pattern

### TopLevelPropertyNaming

TODO: Specify description

#### Configuration options:

* `constantPattern` (default: `'[A-Z][_A-Z0-9]*'`)

   naming pattern

* `propertyPattern` (default: `'[a-z][A-Za-z\d]*'`)

   naming pattern

* `privatePropertyPattern` (default: `'(_)?[a-z][A-Za-z0-9]*'`)

   naming pattern

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
