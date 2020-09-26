---
title: report -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[DefaultContext](index.md)/[report](report.md)



# report  
[jvm]  
Brief description  




Reports a single code smell finding.



Before adding a finding, it is checked if it is not suppressed by @Suppress or @SuppressWarnings annotations.



  
Content  
open override fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  


[jvm]  
Brief description  




Reports a list of code smell findings.



Before adding a finding, it is checked if it is not suppressed by @Suppress or @SuppressWarnings annotations.



  
Content  
open override fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  



