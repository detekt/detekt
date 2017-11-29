# style

The Style ruleset provides rules that assert the style of the code.
This will help keep code in line with the given
code style guidelines.

## Content

1. CollapsibleIfStatements
2. ReturnCount
3. ThrowsCount
4. NewLineAtEndOfFile
5. WildcardImport
6. MaxLineLength
7. EqualsNullCall
8. ForbiddenComment
9. ForbiddenImport
10. FunctionOnlyReturningConstant
11. SpacingBetweenPackageAndImports
12. LoopWithTooManyJumpStatements
13. MethodNameEqualsClassName
14. VariableNaming
15. VariableMinLength
16. VariableMaxLength
17. TopLevelPropertyNaming
18. ObjectPropertyNaming
19. PackageNaming
20. ClassNaming
21. EnumNaming
22. FunctionNaming
23. FunctionMaxLength
24. FunctionMinLength
25. ForbiddenClassName
26. SafeCast
27. UnnecessaryAbstractClass
28. UnnecessaryParentheses
29. UnnecessaryInheritance
30. UtilityClassWithPublicConstructor
31. OptionalAbstractKeyword
32. OptionalWhenBraces
33. OptionalReturnKeyword
34. OptionalUnit
35. ProtectedMemberInFinalClass
36. SerialVersionUIDInSerializableClass
37. MagicNumber
38. ModifierOrder
39. DataClassContainsFunctions
40. UseDataClass
41. UnusedImports
42. ExpressionBodySyntax
43. NestedClassesVisibility
44. RedundantVisibilityModifierRule


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

### NewLineAtEndOfFile

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

### TopLevelPropertyNaming

TODO: Specify description

### ObjectPropertyNaming

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
