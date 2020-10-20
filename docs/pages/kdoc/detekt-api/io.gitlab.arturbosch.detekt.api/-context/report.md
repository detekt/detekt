---
title: report -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Context](index.md)/[report](report.md)



# report  
[jvm]  
Content  
open fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = emptySet(), ruleSetId: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)? = null)  
More info  


Reports a single new violation. By contract the implementation can check if this finding is already suppressed and should not get reported. An alias set can be given to additionally check if an alias was used when suppressing. Additionally suppression by rule set id is supported.

  


[jvm]  
Content  
open fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)> = emptySet(), ruleSetId: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)? = null)  
More info  


Same as [report](report.md) but reports a list of findings.

  



