---
title: Location -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Location](index.md)



# Location  
 [jvm] 

Specifies a position within a source code fragment.

data class [Location](index.md)(**source**: [SourceLocation](../-source-location/index.md), **text**: [TextLocation](../-text-location/index.md), **file**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Compactable](../-compactable/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [Location](-location.md)|  [jvm] ~~fun~~ [~~Location~~](-location.md)~~(~~~~source~~~~:~~ [SourceLocation](../-source-location/index.md)~~,~~ ~~text~~~~:~~ [TextLocation](../-text-location/index.md)~~,~~ ~~locationString~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~file~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~)~~   <br>
| [Location](-location.md)|  [jvm] fun [Location](-location.md)(source: [SourceLocation](../-source-location/index.md), text: [TextLocation](../-text-location/index.md), file: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compact](compact.md)| [jvm]  <br>Brief description  <br><br><br>Contract to format implementing object to a string representation.<br><br>  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [compactWithSignature](../-compactable/compact-with-signature.md)| [jvm]  <br>Brief description  <br><br><br>Same as [compact](compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.<br><br>  <br>Content  <br>open override fun [compactWithSignature](../-compactable/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [SourceLocation](../-source-location/index.md)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [TextLocation](../-text-location/index.md)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(source: [SourceLocation](../-source-location/index.md), text: [TextLocation](../-text-location/index.md), file: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Location](index.md)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [file](index.md#io.gitlab.arturbosch.detekt.api/Location/file/#/PointingToDeclaration/)|  [jvm] val [file](index.md#io.gitlab.arturbosch.detekt.api/Location/file/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [source](index.md#io.gitlab.arturbosch.detekt.api/Location/source/#/PointingToDeclaration/)|  [jvm] val [source](index.md#io.gitlab.arturbosch.detekt.api/Location/source/#/PointingToDeclaration/): [SourceLocation](../-source-location/index.md)   <br>
| [text](index.md#io.gitlab.arturbosch.detekt.api/Location/text/#/PointingToDeclaration/)|  [jvm] val [text](index.md#io.gitlab.arturbosch.detekt.api/Location/text/#/PointingToDeclaration/): [TextLocation](../-text-location/index.md)   <br>

