# complexity

This rule set contains rules that report complex code.

## Content

1. [ComplexCondition](#complexcondition)
2. [ComplexInterface](#complexinterface)
3. [ComplexMethod](#complexmethod)
4. [LabeledExpression](#labeledexpression)
5. [LargeClass](#largeclass)
6. [LongMethod](#longmethod)
7. [LongParameterList](#longparameterlist)
8. [MethodOverloading](#methodoverloading)
9. [NestedBlockDepth](#nestedblockdepth)
10. [StringLiteralDuplication](#stringliteralduplication)
11. [TooManyFunctions](#toomanyfunctions)
## Rules in the `complexity` rule set:

### ComplexCondition

TODO: Specify description

#### Configuration options:

* `threshold` (default: `3`)

   

#### Noncompliant Code:

```kotlin
val str = "foo"
val isFoo = if (str.startsWith("foo") && !str.endsWith("foo") && !str.endsWith("bar") && !str.endsWith("_")) {
    // ...
}
```

#### Compliant Code:

```kotlin
val str = "foo"
val isFoo = if (str.startsWith("foo") && hasCorrectEnding()) {
    // ...
}

fun hasCorrectEnding() = return !str.endsWith("foo") && !str.endsWith("bar") && !str.endsWith("_")
```

### ComplexInterface

TODO: Specify description

#### Configuration options:

* `threshold` (default: `10`)

   maximum amount of definitions in an interface

* `includeStaticDeclarations` (default: `false`)

   whether static declarations should be included

### ComplexMethod

TODO: Specify description

#### Configuration options:

* `threshold` (default: `10`)

   maximum amount of functions in a class

### LabeledExpression

TODO: Specify description

#### Noncompliant Code:

```kotlin
val range = listOf<String>("foo", "bar")
loop@ for (r in range) {
    if (r == "bar") break@loop
    println(r)
}
```

#### Compliant Code:

```kotlin
val range = listOf<String>("foo", "bar")
for (r in range) {
    if (r == "bar") break
    println(r)
}
```

### LargeClass

TODO: Specify description

#### Configuration options:

* `threshold` (default: `150`)

   maximum size of a class

### LongMethod

TODO: Specify description

#### Configuration options:

* `threshold` (default: `20`)

   maximum lines in a method

### LongParameterList

Reports functions which have more parameters then a certain threshold (default: 5).

#### Configuration options:

* `threshold` (default: `5`)

   maximum number of parameters

* `ignoreDefaultParameters` (default: `false`)

   ignore parameters that have a default value

### MethodOverloading

TODO: Specify description

#### Configuration options:

* `threshold` (default: `5`)

   

### NestedBlockDepth

TODO: Specify description

#### Configuration options:

* `threshold` (default: `3`)

   maximum nesting depth

### StringLiteralDuplication

TODO: Specify description

#### Configuration options:

* `threshold` (default: `2`)

   maximum allowed duplication

* `ignoreAnnotation` (default: `true`)

   if values in Annotations should be ignored

* `excludeStringsWithLessThan5Characters` (default: `true`)

   if short strings should be excluded

* `ignoreStringsRegex` (default: `'$^'`)

   RegEx of Strings that should be ignored

#### Noncompliant Code:

```kotlin
class Foo {

    val s1 = "lorem"
    fun bar(s: String = "lorem") {
        s1.equals("lorem")
    }
}
```

#### Compliant Code:

```kotlin
class Foo {
    val lorem = "lorem"
    val s1 = lorem
    fun bar(s: String = lorem) {
        s1.equals(lorem)
    }
}
```

### TooManyFunctions

TODO: Specify description

#### Configuration options:

* `thresholdInFiles` (default: `10`)

   threshold in files

* `thresholdInClasses` (default: `10`)

   threshold in classes

* `thresholdInInterfaces` (default: `10`)

   threshold in interfaces

* `thresholdInObjects` (default: `10`)

   threshold in objects

* `thresholdInEnums` (default: `10`)

   threshold in enums
