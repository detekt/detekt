# comments

This rule set provides rules that address issues in comments and documentation
of the code.

## Content

1. [CommentOverPrivateFunction](#CommentOverPrivateFunction)
2. [CommentOverPrivateProperty](#CommentOverPrivateProperty)
3. [UndocumentedPublicClass](#UndocumentedPublicClass)
4. [UndocumentedPublicFunction](#UndocumentedPublicFunction)
## Rules in the `comments` rule set:

### CommentOverPrivateFunction

This rule reports comments and documentation that has been added to private functions. These comments get reported
because they probably explain the functionality of the private function. However private functions should be small
enough and have an understandable name that they are self-explanatory and do not need this comment in the first
place.

Instead of simply removing this comment to solve this issue prefer to split up the function into smaller functions
with better names if necessary. Giving the function a better, more descriptive name can also help solve this issue.

### CommentOverPrivateProperty

This rule reports comments and documentation above private properties. This can indicated that the property has a
confusing name or is not in a small enough context to be understood.
Private properties should be named in a self-explanatory way and readers of the code should be able to understand
why the property exists and what purpose it solves without the comment.

Instead of simply removing the comment to solve this issue prefer renaming the property to a more self-explanatory
name. Refactoring the code and splitting up the class this property resides in can also be an option.

### UndocumentedPublicClass

This rule report public classes, objects and interfaces which do not have the required documentation.
Enable this rule if the codebase should have documentation on every public class, interface and object.

With the configuration options the rule can be configured on its behavior to search inner objects.

#### Configuration options:

* `searchInNestedClass` (default: `true`)

   if nested classes should be searched

* `searchInInnerClass` (default: `true`)

   if inner classes should be searched

* `searchInInnerObject` (default: `true`)

   if inner objects should be searched

* `searchInInnerInterface` (default: `true`)

   if inner interfaces should be searched

### UndocumentedPublicFunction

This rule will report any public function which does not have the required documentation.
If the codebase should have documentation on all public functions enable this rule to enforce this.
