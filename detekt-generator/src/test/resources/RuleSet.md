Rule Set ID: `style`

style rule set

#### Configuration options:

* ``rulesetconfig1`` (default: ``true``)

  description rulesetconfig1

* ``rulesetconfig2`` (default: ``['foo', 'bar']``)

  description rulesetconfig2

* ~~``deprecatedSimpleConfig``~~ (default: ``true``)

  **Deprecated**: is deprecated

  description deprecatedSimpleConfig

* ~~``deprecatedListConfig``~~ (default: ``['foo', 'bar']``)

  **Deprecated**: is deprecated

  description deprecatedListConfig

* ``rulesetconfig3`` (default: ``['first', 'se*cond']``)

  description rulesetconfig2

### MagicNumber

a wildcard import

**Active by default**: Yes - Since v1.0.0

**Aliases**: alias1, alias2

#### Configuration options:

* ``conf1`` (default: ``'foo'``)

  a config option

* ~~``conf2``~~ (default: ``false``)

  **Deprecated**: use conf1 instead

  deprecated config

* ``conf3`` (default: ``['a', 'b']``)

  list config

* ~~``conf4``~~ (default: ``['a', 'b']``)

  **Deprecated**: use conf3 instead

  deprecated list config

* ``conf5`` (default: ``120``) (android default: ``100``)

  rule with android variants

#### Noncompliant Code:

```kotlin
import foo.*
```

#### Compliant Code:

```kotlin
import foo.bar
```

### EqualsNull

equals null

**Active by default**: No

### NoUnitKeyword

removes :Unit

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
fun stuff(): Unit {}
```

#### Compliant Code:

```kotlin
fun stuff() {}
```

### ~~DuplicateCaseInWhenExpression~~

is deprecated

Duplicated `case` statements in a `when` expression detected.

**Active by default**: Yes - Since v1.16.0

#### Noncompliant Code:

```kotlin
fun stuff(): Unit {}
```

#### Compliant Code:

```kotlin
fun stuff() {}
```
