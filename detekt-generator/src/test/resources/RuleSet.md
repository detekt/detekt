# style

style rule set

## Content

1. [WildcardImport](#WildcardImport)
2. [EqualsNull](#EqualsNull)
## Rules in the `style` rule set:

### WildcardImport

a wildcard import

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
