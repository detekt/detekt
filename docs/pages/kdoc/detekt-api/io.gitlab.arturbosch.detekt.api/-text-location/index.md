---
title: TextLocation -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[TextLocation](index.md)



# TextLocation  
 [jvm] 

Stores character start and end positions of an text file.

data class [TextLocation](index.md)(**start**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **end**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [TextLocation](-text-location.md)|  [jvm] fun [TextLocation](-text-location.md)(start: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), end: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(start: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), end: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [TextLocation](index.md)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [end](index.md#io.gitlab.arturbosch.detekt.api/TextLocation/end/#/PointingToDeclaration/)|  [jvm] val [end](index.md#io.gitlab.arturbosch.detekt.api/TextLocation/end/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [start](index.md#io.gitlab.arturbosch.detekt.api/TextLocation/start/#/PointingToDeclaration/)|  [jvm] val [start](index.md#io.gitlab.arturbosch.detekt.api/TextLocation/start/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

