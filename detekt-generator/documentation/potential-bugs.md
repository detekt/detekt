# potential-bugs

The potential-bugs rule set provides rules that detect potential bugs.

## Content

1. [DuplicateCaseInWhenExpression](#DuplicateCaseInWhenExpression)
2. [EqualsAlwaysReturnsTrueOrFalse](#EqualsAlwaysReturnsTrueOrFalse)
3. [EqualsWithHashCodeExist](#EqualsWithHashCodeExist)
4. [IteratorNotThrowingNoSuchElementException](#IteratorNotThrowingNoSuchElementException)
5. [IteratorHasNextCallsNextMethod](#IteratorHasNextCallsNextMethod)
6. [UselessPostfixExpression](#UselessPostfixExpression)
7. [InvalidLoopCondition](#InvalidLoopCondition)
8. [WrongEqualsTypeParameter](#WrongEqualsTypeParameter)
9. [ExplicitGarbageCollectionCall](#ExplicitGarbageCollectionCall)
10. [LateinitUsage](#LateinitUsage)
11. [UnconditionalJumpStatementInLoop](#UnconditionalJumpStatementInLoop)
12. [UnreachableCode](#UnreachableCode)
13. [UnsafeCallOnNullableType](#UnsafeCallOnNullableType)
14. [UnsafeCast](#UnsafeCast)
## Rules in the `potential-bugs` rule set:

### DuplicateCaseInWhenExpression

Flags duplicate case statements in when expressions.

If a when expression contains the same case statement multiple times they should be merged. Otherwise it might be
easy to miss one of the cases when reading the code, leading to unwanted side effects.

### EqualsAlwaysReturnsTrueOrFalse

Reports equals() methods which will always return true or false.

Equals methods should always report if some other object is equal to the current object.
See the Kotlin documentation for Any.equals(other: Any?):
https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html

### EqualsWithHashCodeExist

When a class overrides the equals() method it should also override the hashCode() method.

All hash-based collections depend on objects meeting the equals-contract. Two equal objects must produce the
same hashcode. When inheriting equals or hashcode, override the inherited and call the super method for
clarification.

### IteratorNotThrowingNoSuchElementException

Reports implementations of the Iterator interface which do not throw a NoSuchElementException in the
implementation of the next() method. When there are no more elements to return an Iterator should throw a
NoSuchElementException.

See: https://docs.oracle.com/javase/7/docs/api/java/util/Iterator.html#next()

### IteratorHasNextCallsNextMethod

Verifies implementations of the Iterator interface.
The hasNext() method of an Iterator implementation should not have any side effects.
This rule reports implementations that call the next() method of the Iterator inside the hasNext() method.

### UselessPostfixExpression

This rule reports postfix expressions (++, --) which are unused and thus unnecessary.
This leads to confusion as a reader of the code might think the value will be incremented/decremented.
However the value is replaced with the original value which might lead to bugs.

### InvalidLoopCondition

Reports loop conditions which will never be triggered.
This might be due to invalid ranges like (10..9) which will cause the loop to never be entered.

### WrongEqualsTypeParameter

Reports equals() methods which take in a wrongly typed parameter.
Correct implementations of the equals() method should only take in a parameter of type Any?
See: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html

### ExplicitGarbageCollectionCall

Reports all calls to explicitly trigger the Garbage Collector.
Code should work independently of the garbage collector and should not require the GC to be triggered in certain
points in time.

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

Reports loops which contain jump statements that jump regardless of any conditions.
This implies that the loop is only executed once and thus could be rewritten without a
loop alltogether.

### UnreachableCode

Reports unreachable code.
Code can be unreachable because it is behind return, throw, continue or break expressions.
This unreachable code should be removed as it serves no purpose.

### UnsafeCallOnNullableType

Reports unsafe calls on nullable types. These calls will throw a NullPointerException in case
the nullable value is null. Kotlin provides many ways to work with nullable types to increase
null safety. Guard the code appropriately to prevent NullPointerExceptions.

### UnsafeCast

Reports casts which are unsafe. In case the cast is not possible it will throw an exception.
