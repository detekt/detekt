---
title: DefaultContext -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[DefaultContext](index.md)



# DefaultContext  
 [jvm] 

Default [Context](../-context/index.md) implementation.

open class [DefaultContext](index.md) : [Context](../-context/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [DefaultContext](-default-context.md)|  [jvm] fun [DefaultContext](-default-context.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [clearFindings](clear-findings.md)| [jvm]  <br>Brief description  <br><br><br>Clears previous findings. Normally this is done on every new KtFile analyzed and should be called by clients.<br><br>  <br>Content  <br>override fun [clearFindings](clear-findings.md)()  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [report](report.md)| [jvm]  <br>Brief description  <br><br><br><br><br>Reports a single code smell finding.<br><br><br><br>Before adding a finding, it is checked if it is not suppressed by @Suppress or @SuppressWarnings annotations.<br><br><br><br>  <br>Content  <br>open override fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  <br><br><br>[jvm]  <br>Brief description  <br><br><br><br><br>Reports a list of code smell findings.<br><br><br><br>Before adding a finding, it is checked if it is not suppressed by @Suppress or @SuppressWarnings annotations.<br><br><br><br>  <br>Content  <br>open override fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)?)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [findings](index.md#io.gitlab.arturbosch.detekt.api/DefaultContext/findings/#/PointingToDeclaration/)|  [jvm] <br><br>Returns a copy of violations for this rule.<br><br>open override val [findings](index.md#io.gitlab.arturbosch.detekt.api/DefaultContext/findings/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>   <br>

