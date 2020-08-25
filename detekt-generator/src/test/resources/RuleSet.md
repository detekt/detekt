style rule set

### WildcardImport

a wildcard import

**Severity**: Defect

**Debt**: 10min

**Aliases**: alias1, alias2

#### Configuration options:

* ``conf1`` (default: ``foo``)

   a config option

* ~~``conf2``~~ (default: ``false``)

   **Deprecated**: use conf1 instead

   deprecated config

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

### NoUnitKeyword

removes :Unit

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
