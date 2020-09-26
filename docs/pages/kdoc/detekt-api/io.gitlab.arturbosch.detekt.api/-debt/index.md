---
title: Debt -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Debt](index.md)



# Debt  
 [jvm] 

Debt describes the estimated amount of work needed to fix a given issue.

data class [Debt](index.md)(**days**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **hours**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **mins**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [Debt](-debt.md)|  [jvm] fun [Debt](-debt.md)(days: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), hours: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), mins: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(days: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), hours: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), mins: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Debt](index.md)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [plus](plus.md)| [jvm]  <br>Brief description  <br><br><br>Adds the other debt to this debt. This recalculates the potential overflow resulting from the addition.<br><br>  <br>Content  <br>operator fun [plus](plus.md)(other: [Debt](index.md)): [Debt](index.md)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [days](index.md#io.gitlab.arturbosch.detekt.api/Debt/days/#/PointingToDeclaration/)|  [jvm] val [days](index.md#io.gitlab.arturbosch.detekt.api/Debt/days/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [hours](index.md#io.gitlab.arturbosch.detekt.api/Debt/hours/#/PointingToDeclaration/)|  [jvm] val [hours](index.md#io.gitlab.arturbosch.detekt.api/Debt/hours/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [mins](index.md#io.gitlab.arturbosch.detekt.api/Debt/mins/#/PointingToDeclaration/)|  [jvm] val [mins](index.md#io.gitlab.arturbosch.detekt.api/Debt/mins/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

