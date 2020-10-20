---
title: isSuppressedBy -
---
//[detekt-api](../index.md)/[io.gitlab.arturbosch.detekt.api.internal](index.md)/[isSuppressedBy](is-suppressed-by.md)



# isSuppressedBy  
[jvm]  
Content  
fun KtElement.[isSuppressedBy](is-suppressed-by.md)(id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../io.gitlab.arturbosch.detekt.api/index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)? = null): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  
More info  


Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations. If this element cannot have annotations, the first annotative parent is searched.

  


[jvm]  
Content  
fun KtAnnotated.[isSuppressedBy](is-suppressed-by.md)(id: [RuleId](../io.gitlab.arturbosch.detekt.api/index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../io.gitlab.arturbosch.detekt.api/index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)? = null): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  
More info  


Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.

  



