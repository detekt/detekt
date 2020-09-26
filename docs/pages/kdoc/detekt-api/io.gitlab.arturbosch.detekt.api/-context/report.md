---
title: report -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Context](index.md)/[report](report.md)



# report  
[jvm]  
Brief description  


Reports a single new violation. By contract the implementation can check if this finding is already suppressed and should not get reported. An alias set can be given to additionally check if an alias was used when suppressing. Additionally suppression by rule set id is supported.

  
Content  
open fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  


[jvm]  
Brief description  


Same as [report](report.md) but reports a list of findings.

  
Content  
open fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  



