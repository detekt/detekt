style rule set

### MagicNumber

a wildcard import

**Active by default**: Yes - Since v1.0.0

**Debt**: 10min

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

**Debt**: 5m

#### Noncompliant Code:

```kotlin
fun stuff(): Unit {}
```

#### Compliant Code:

```kotlin
fun stuff() {}
```

### DuplicateCaseInWhenExpression

Duplicated `case` statements in a `when` expression detected.

**Active by default**: Yes - Since v1.16.0

**Debt**: 5m

#### Noncompliant Code:

```kotlin
fun stuff(): Unit {}
```

#### Compliant Code:

```kotlin
fun stuff() {}
```
