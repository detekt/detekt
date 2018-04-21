style rule set

### WildcardImport

a wildcard import

**Severity**: Defect

**Debt**: 10min

#### Configuration options:

* `conf1` (default: `foo`)

   a config option

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

**Debt**: 5m

#### Noncompliant Code:

```kotlin
fun stuff(): Unit {}
```

#### Compliant Code:

```kotlin
fun stuff() {}
```
