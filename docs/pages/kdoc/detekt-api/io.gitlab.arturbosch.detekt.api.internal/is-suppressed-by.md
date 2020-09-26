---
title: isSuppressedBy -
---
//[detekt-api](../index.md)/[io.gitlab.arturbosch.detekt.api.internal](index.md)/[isSuppressedBy](is-suppressed-by.md)



# isSuppressedBy  
[jvm]  
Brief description  


Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations. If this element cannot have annotations, the first annotative parent is searched.

  
Content  
fun KtElement.[isSuppressedBy](is-suppressed-by.md)(id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../io.gitlab.arturbosch.detekt.api/index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  


[jvm]  
Brief description  


Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.

  
Content  
fun KtAnnotated.[isSuppressedBy](is-suppressed-by.md)(id: [RuleId](../io.gitlab.arturbosch.detekt.api/index.md#io.gitlab.arturbosch.detekt.api/RuleId///PointingToDeclaration/), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../io.gitlab.arturbosch.detekt.api/index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  



