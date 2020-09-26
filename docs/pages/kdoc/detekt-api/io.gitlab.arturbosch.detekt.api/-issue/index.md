---
title: Issue -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Issue](index.md)



# Issue  
 [jvm] 

An issue represents a problem in the codebase.

data class [Issue](index.md)(**id**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **severity**: [Severity](../-severity/index.md), **description**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **debt**: [Debt](../-debt/index.md))   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [Issue](-issue.md)|  [jvm] fun [Issue](-issue.md)(id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), severity: [Severity](../-severity/index.md), description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), debt: [Debt](../-debt/index.md))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Severity](../-severity/index.md)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component4](component4.md)| [jvm]  <br>Content  <br>operator fun [component4](component4.md)(): [Debt](../-debt/index.md)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(id: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), severity: [Severity](../-severity/index.md), description: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), debt: [Debt](../-debt/index.md)): [Issue](index.md)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [debt](index.md#io.gitlab.arturbosch.detekt.api/Issue/debt/#/PointingToDeclaration/)|  [jvm] val [debt](index.md#io.gitlab.arturbosch.detekt.api/Issue/debt/#/PointingToDeclaration/): [Debt](../-debt/index.md)   <br>
| [description](index.md#io.gitlab.arturbosch.detekt.api/Issue/description/#/PointingToDeclaration/)|  [jvm] val [description](index.md#io.gitlab.arturbosch.detekt.api/Issue/description/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [id](index.md#io.gitlab.arturbosch.detekt.api/Issue/id/#/PointingToDeclaration/)|  [jvm] val [id](index.md#io.gitlab.arturbosch.detekt.api/Issue/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [severity](index.md#io.gitlab.arturbosch.detekt.api/Issue/severity/#/PointingToDeclaration/)|  [jvm] val [severity](index.md#io.gitlab.arturbosch.detekt.api/Issue/severity/#/PointingToDeclaration/): [Severity](../-severity/index.md)   <br>

