---
title:  "Howto: migrating from the *test-pattern*"
published: true
permalink: howto-migratetestpattern.html
summary: "Starting with RC15 the test-pattern is obsolete. This post shows how to leverage rule path excludes to achieve the same functionality."
tags: [guides]
---

Starting with RC15 the test-pattern is obsolete. This post shows how to leverage rule path excludes to achieve the same functionality.

With version < RC15 the configuration file allowed to specify
which paths should be mapped to test files so detekt would not run
specific rule sets and rules on these test files:

```yaml
test-pattern: # Configure exclusions for test sources
  active: true
  patterns: # Test file regexes
    - '.*/test/.*'
    - '.*Test.kt'
    - '.*Spec.kt'
  exclude-rule-sets:
    - 'comments'
  exclude-rules:
    - 'NamingRules'
    - 'WildcardImport'
    - 'MagicNumber'
    - 'MaxLineLength'
    - 'LateinitUsage'
    - 'StringLiteralDuplication'
    - 'SpreadOperator'
    - 'TooManyFunctions'
```

This was an okay approach as we nowadays separate production code and test code.
However more different kinds of source files can be identified.
For example generated and library code.

With the new approach of offering path patterns on the rule and rule set level the user has much more freedom
in defining which rule should run on which path.

If we do not care about documented test classes because we write our test code
in a *documenting way*, we could simply exclude the `comments` rule set for following patterns:
```
comments:
  ...
  excludes: "**/*Test.kt, **/*Spec.kt"
```

If we for example do not care about `MagicNumber`'s in test code, we can
exclude our test files for this rule:
```
style:
  ...
  MagicNumber:
    excludes: "**/*Test.kt, **/*Spec.kt"
```

Make sure to use globing patterns here as detekt does not support regular expressions anymore.
This change was done to make use of the `java.nio.file` library when handling os-specific paths.

If you were using the default detekt configuration with the default test-pattern,
you will not notice any changes when upgrading to >= RC15.
All *exclude-rules* and *exclude-rule-sets* will now make use of `excludes: "**/test/**,**/*Test.kt,**/*Spec.kt"`.
