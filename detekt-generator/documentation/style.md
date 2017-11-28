# style

The Style ruleset provides rules that assert the style of the code.
This will help keep code in line with the given
code style guidelines.

## Rules in the `style` rule set:

### CollapsibleIfStatements

TODO: Specify description

### ReturnCount

Restrict the number of return methods allowed in methods.

Having many exit points in a function can be confusing and impacts readability of the
code.

#### Configuration options:

* `max` (default: `2`)

   define the maximum number of return statements allowed per function

### ThrowsCount

TODO: Specify description

### WildcardImport

Wildcard imports should be replaced with imports using fully qualified class names. This helps increase clarity of
which classes are imported and helps prevent naming conflicts.

Library updates can introduce naming clashes with your own classes which might result in compilation errors.

#### Configuration options:

* `excludeImports` (default: `""`)

   Define a whitelist of package names that should be allowed to be imported
with wildcard imports.

### MaxLineLength

TODO: Specify description

### EqualsNullCall

TODO: Specify description

### ForbiddenComment

TODO: Specify description

### ForbiddenImport

TODO: Specify description

### FunctionOnlyReturningConstant

TODO: Specify description

### SpacingBetweenPackageAndImports

TODO: Specify description

### LoopWithTooManyJumpStatements

TODO: Specify description

### MethodNameEqualsClassName

TODO: Specify description

### VariableNaming

TODO: Specify description

### VariableMinLength

TODO: Specify description

### VariableMaxLength

TODO: Specify description

### ConstantNaming

TODO: Specify description

### PackageNaming

TODO: Specify description

### ClassNaming

TODO: Specify description

### EnumNaming

TODO: Specify description

### FunctionNaming

TODO: Specify description

### FunctionMaxLength

TODO: Specify description

### FunctionMinLength

TODO: Specify description

### ForbiddenClassName

TODO: Specify description

### SafeCast

TODO: Specify description

### UnnecessaryAbstractClass

TODO: Specify description

### UnnecessaryParentheses

Reports unnecessary parentheses around expressions.

Added in v1.0.0.RC4

### UnnecessaryInheritance

TODO: Specify description

### UtilityClassWithPublicConstructor

TODO: Specify description

### OptionalAbstractKeyword

TODO: Specify description

### OptionalWhenBraces

TODO: Specify description

### OptionalReturnKeyword

TODO: Specify description

### OptionalUnit

TODO: Specify description

### ProtectedMemberInFinalClass

TODO: Specify description

### SerialVersionUIDInSerializableClass

TODO: Specify description

### MagicNumber

TODO: Specify description

### ModifierOrder

Modifier order array taken from ktlint: https://github.com/shyiko/ktlint

### DataClassContainsFunctions

TODO: Specify description

### UseDataClass

TODO: Specify description

### UnusedImports

TODO: Specify description

### ExpressionBodySyntax

TODO: Specify description

### NestedClassesVisibility

TODO: Specify description

### RedundantVisibilityModifierRule

TODO: Specify description
