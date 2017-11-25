# potential-bugs

The potential-bugs rule set provides rules that detect potential bugs.

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

### UnconditionalJumpStatementInLoop

TODO: Specify description

### UnreachableCode

TODO: Specify description

### UnsafeCallOnNullableType

TODO: Specify description

### UnsafeCast

TODO: Specify description
