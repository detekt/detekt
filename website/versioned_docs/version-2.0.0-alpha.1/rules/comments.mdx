---
title: Comments Rule Set
sidebar: home_sidebar
keywords: [rules, comments]
permalink: comments.html
toc: true
folder: documentation
---
This rule set provides rules that address issues in comments and documentation
of the code.

### AbsentOrWrongFileLicense

This rule will report every Kotlin source file which doesn't have the required license header.
The rule validates each Kotlin source and operates in two modes: if `licenseTemplateIsRegex = false` (or missing)
the rule checks whether the input file header starts with the read text from the passed file in the
`licenseTemplateFile` configuration option. If `licenseTemplateIsRegex = true` the rule matches the header with
a regular expression produced from the passed template license file (defined via `licenseTemplateFile` configuration
option).

**Active by default**: No

#### Configuration options:

* ``licenseTemplateFile`` (default: ``'license.template'``)

  path to file with license header template resolved relatively to config file

* ``licenseTemplateIsRegex`` (default: ``false``)

  whether or not the license header template is a regex template

### CommentOverPrivateFunction

This rule reports comments and documentation that has been added to private functions. These comments get reported
because they probably explain the functionality of the private function. However, private functions should be small
enough and have an understandable name so that they are self-explanatory and do not need this comment in the first
place.

Instead of simply removing this comment to solve this issue prefer to split up the function into smaller functions
with better names if necessary. Giving the function a better, more descriptive name can also help in
solving this issue.

**Active by default**: No

### CommentOverPrivateProperty

This rule reports comments and documentation above private properties. This can indicate that the property has a
confusing name or is not in a small enough context to be understood.
Private properties should be named in a self-explanatory way and readers of the code should be able to understand
why the property exists and what purpose it solves without the comment.

Instead of simply removing the comment to solve this issue, prefer renaming the property to a more self-explanatory
name. If this property is inside a bigger class, it makes sense to refactor and split up the class. This can
increase readability and make the documentation obsolete.

**Active by default**: No

### DeprecatedBlockTag

This rule reports use of the `@deprecated` block tag in KDoc comments. Deprecation must be specified using a
`@Deprecated` annotation as adding a `@deprecated` block tag in KDoc comments
[has no effect and is not supported](https://kotlinlang.org/docs/kotlin-doc.html#suppress). The `@Deprecated`
annotation constructor has dedicated fields for a message and a type (warning, error, etc.). You can also use the
`@ReplaceWith` annotation to specify how to solve the deprecation automatically via the IDE.

**Active by default**: No

#### Noncompliant Code:

```kotlin
/**
* This function prints a message followed by a new line.
*
* @deprecated Useless, the Kotlin standard library can already do this. Replace with println.
*/
fun printThenNewline(what: String) {
    // ...
}
```

#### Compliant Code:

```kotlin
/**
* This function prints a message followed by a new line.
*/
@Deprecated("Useless, the Kotlin standard library can already do this.")
@ReplaceWith("println(what)")
fun printThenNewline(what: String) {
    // ...
}
```

### EndOfSentenceFormat

This rule validates the end of the first sentence of a KDoc comment.
It should end with proper punctuation or with a correct URL.

**Active by default**: No

#### Configuration options:

* ``endOfSentenceFormat`` (default: ``'([.?!][ \t\n\r\f<])|([.?!:]$)'``)

  regular expression which should match the end of the first sentence in the KDoc

### KDocReferencesNonPublicProperty

This rule will report any KDoc comments that refer to non-public properties of a class.
Clients do not need to know the implementation details.

**Active by default**: No

#### Noncompliant Code:

```kotlin
/**
* Comment
* [prop1] - non-public property
* [prop2] - public property
*/
class Test {
    private val prop1 = 0
    val prop2 = 0
}
```

#### Compliant Code:

```kotlin
/**
* Comment
* [prop2] - public property
*/
class Test {
    private val prop1 = 0
    val prop2 = 0
}
```

### OutdatedDocumentation

This rule will report any class, function or constructor with KDoc that does not match the declaration signature.
If KDoc is not present or does not contain any @param or @property tags, rule violation will not be reported.
By default, both type and value parameters need to be matched and declarations orders must be preserved. You can
turn off these features using configuration options.

**Active by default**: No

#### Configuration options:

* ``matchTypeParameters`` (default: ``true``)

  if type parameters should be matched

* ``matchDeclarationsOrder`` (default: ``true``)

  if the order of declarations should be preserved

* ``allowParamOnConstructorProperties`` (default: ``false``)

  if we allow constructor parameters to be marked as @param instead of @property

#### Noncompliant Code:

```kotlin
/**
* @param someParam
* @property someProp
*/
class MyClass(otherParam: String, val otherProp: String)

/**
* @param T
* @param someParam
*/
fun <T, S> myFun(someParam: String)
```

#### Compliant Code:

```kotlin
/**
* @param someParam
* @property someProp
*/
class MyClass(someParam: String, val someProp: String)

/**
* @param T
* @param S
* @param someParam
*/
fun <T, S> myFun(someParam: String)
```

### UndocumentedPublicClass

This rule reports public classes, objects and interfaces which do not have the required documentation.
Enable this rule if the codebase should have documentation on every public class, interface and object.

By default, this rule also searches for nested and inner classes and objects. This default behavior can be changed
with the configuration options of this rule.

**Active by default**: No

#### Configuration options:

* ``searchInNestedClass`` (default: ``true``)

  if nested classes should be searched

* ``searchInInnerClass`` (default: ``true``)

  if inner classes should be searched

* ``searchInInnerObject`` (default: ``true``)

  if inner objects should be searched

* ``searchInInnerInterface`` (default: ``true``)

  if inner interfaces should be searched

* ``searchInProtectedClass`` (default: ``false``)

  if protected classes should be searched

* ``ignoreDefaultCompanionObject`` (default: ``false``)

  whether default companion objects should be exempted

### UndocumentedPublicFunction

This rule will report any public function which does not have the required documentation.
If the codebase should have documentation on all public functions enable this rule to enforce this.
Overridden functions are excluded by this rule.

**Active by default**: No

#### Configuration options:

* ``searchProtectedFunction`` (default: ``false``)

  if protected functions should be searched

### UndocumentedPublicProperty

This rule will report any public property which does not have the required documentation.
This also includes public properties defined in a primary constructor.
If the codebase should have documentation on all public properties enable this rule to enforce this.
Overridden properties are excluded by this rule.

**Active by default**: No

#### Configuration options:

* ``searchProtectedProperty`` (default: ``false``)

  if protected functions should be searched

* ``ignoreEnumEntries`` (default: ``false``)

  ignores a enum entries when set to true
