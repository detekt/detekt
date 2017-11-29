# potential-bugs

The potential-bugs rule set provides rules that detect potential bugs.

## Content

1. DuplicateCaseInWhenExpression
2. EqualsAlwaysReturnsTrueOrFalse
3. EqualsWithHashCodeExist
4. IteratorNotThrowingNoSuchElementException
5. IteratorHasNextCallsNextMethod
6. UselessPostfixExpression
7. InvalidLoopCondition
8. WrongEqualsTypeParameter
9. ExplicitGarbageCollectionCall
10. LateinitUsage
11. UnconditionalJumpStatementInLoop
12. UnreachableCode
13. UnsafeCallOnNullableType
14. UnsafeCast


## Rules in the `potential-bugs` rule set:

### DuplicateCaseInWhenExpression

TODO: Specify description

### EqualsAlwaysReturnsTrueOrFalse

TODO: Specify description

### EqualsWithHashCodeExist

TODO: Specify description

### IteratorNotThrowingNoSuchElementException

TODO: Specify description

### IteratorHasNextCallsNextMethod

TODO: Specify description

### UselessPostfixExpression

TODO: Specify description

### InvalidLoopCondition

TODO: Specify description

### WrongEqualsTypeParameter

TODO: Specify description

### ExplicitGarbageCollectionCall

TODO: Specify description

### LateinitUsage

Turn on this rule to flag usages of the lateinit modifier.

Using lateinit for property initialization can be error prone and the actual initialization is not
guaranteed. Try using constructor injection or delegation to initialize properties.

#### Configuration options:

* `excludeAnnotatedProperties` (default: `""`)

   Allows you to provide a list of annotations that disable
this check.

* `ignoreOnClassesPattern` (default: `""`)

   Allows you to disable the rule for a list of classes

### UnconditionalJumpStatementInLoop

TODO: Specify description

### UnreachableCode

TODO: Specify description

### UnsafeCallOnNullableType

TODO: Specify description

### UnsafeCast

TODO: Specify description
