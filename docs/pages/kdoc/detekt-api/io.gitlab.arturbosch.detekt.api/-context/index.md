---
title: Context -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Context](index.md)



# Context  
 [jvm] 



A context describes the storing and reporting mechanism of [Finding](../-finding/index.md)'s inside a [Rule](../-rule/index.md). Additionally it handles suppression and aliases management.



The detekt engine retrieves the findings after each KtFile visit and resets the context before the next KtFile.



interface [Context](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [clearFindings](clear-findings.md)| [jvm]  <br>Brief description  <br><br><br>Clears previous findings. Normally this is done on every new KtFile analyzed and should be called by clients.<br><br>  <br>Content  <br>abstract fun [clearFindings](clear-findings.md)()  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [report](report.md)| [jvm]  <br>Brief description  <br><br><br>Reports a single new violation. By contract the implementation can check if this finding is already suppressed and should not get reported. An alias set can be given to additionally check if an alias was used when suppressing. Additionally suppression by rule set id is supported.<br><br>  <br>Content  <br>open fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Same as [report](report.md) but reports a list of findings.<br><br>  <br>Content  <br>open fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [findings](index.md#io.gitlab.arturbosch.detekt.api/Context/findings/#/PointingToDeclaration/)|  [jvm] abstract val [findings](index.md#io.gitlab.arturbosch.detekt.api/Context/findings/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>   <br>


## Inheritors  
  
|  Name| 
|---|
| [DefaultContext](../-default-context/index.md)
| [BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)

