---
title: report -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[DefaultContext](index.md)/[report](report.md)



# report  
[jvm]  
Content  
open override fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)?)  
More info  


Reports a single code smell finding.



Before adding a finding, it is checked if it is not suppressed by @Suppress or @SuppressWarnings annotations.

  


[jvm]  
Content  
open override fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)?)  
More info  


Reports a list of code smell findings.



Before adding a finding, it is checked if it is not suppressed by @Suppress or @SuppressWarnings annotations.

  



