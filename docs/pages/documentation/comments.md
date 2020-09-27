---
title: Comments Rule Set
sidebar: home_sidebar
keywords: rules, comments
permalink: comments.html
toc: true
folder: documentation
---
This rule set provides rules that address issues in comments and documentation
of the code.

### AbsentOrWrongFileLicense

This rule will report every Kotlin source file which doesn't have the required license header.
The rule checks each Kotlin source file whether the header starts with the read text from the passed file in the
`licenseTemplateFile` configuration option.

**Severity**: Maintainability

**Debt**: 5min

#### Configuration options:

* ``licenseTemplateFile`` (default: ``'license.template'``)

   path to file with license header template resolved relatively to config file

### CommentOverPrivateFunction

This rule reports comments and documentation that has been added to private functions. These comments get reported
because they probably explain the functionality of the private function. However private functions should be small
enough and have an understandable name so that they are self-explanatory and do not need this comment in the first
place.

Instead of simply removing this comment to solve this issue prefer to split up the function into smaller functions
with better names if necessary. Giving the function a better, more descriptive name can also help in
solving this issue.

**Severity**: Maintainability

**Debt**: 20min

### CommentOverPrivateProperty

This rule reports comments and documentation above private properties. This can indicate that the property has a
confusing name or is not in a small enough context to be understood.
Private properties should be named in a self-explanatory way and readers of the code should be able to understand
why the property exists and what purpose it solves without the comment.

Instead of simply removing the comment to solve this issue prefer renaming the property to a more self-explanatory
name. If this property is inside a bigger class it could make senes to refactor and split up the class. This can
increase readability and make the documentation obsolete.

**Severity**: Maintainability

**Debt**: 20min

### EndOfSentenceFormat

This rule validates the end of the first sentence of a KDoc comment.
It should end with proper punctuation or with a correct URL.

**Severity**: Maintainability

**Debt**: 5min

#### Configuration options:

* ``endOfSentenceFormat`` (default: ``'([.?!][ \t\n\r\f<])|([.?!:]$)'``)

   regular expression which should match the end of the first sentence in the KDoc

### UndocumentedPublicClass

This rule reports public classes, objects and interfaces which do not have the required documentation.
Enable this rule if the codebase should have documentation on every public class, interface and object.

By default this rule also searches for nested and inner classes and objects. This default behavior can be changed
with the configuration options of this rule.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* ``searchInNestedClass`` (default: ``true``)

   if nested classes should be searched

* ``searchInInnerClass`` (default: ``true``)

   if inner classes should be searched

* ``searchInInnerObject`` (default: ``true``)

   if inner objects should be searched

* ``searchInInnerInterface`` (default: ``true``)

   if inner interfaces should be searched

### UndocumentedPublicFunction

This rule will report any public function which does not have the required documentation.
If the codebase should have documentation on all public functions enable this rule to enforce this.
Overridden functions are excluded by this rule.

**Severity**: Maintainability

**Debt**: 20min

### UndocumentedPublicProperty

This rule will report any public property which does not have the required documentation.
This also includes public properties defined in a primary constructor.
If the codebase should have documentation on all public properties enable this rule to enforce this.
Overridden properties are excluded by this rule.

**Severity**: Maintainability

**Debt**: 20min
