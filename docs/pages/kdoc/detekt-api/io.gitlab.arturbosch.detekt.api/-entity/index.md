---
title: Entity -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Entity](index.md)



# Entity  
 [jvm] 

Stores information about a specific code fragment.

data class [Entity](index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **signature**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **location**: [Location](../-location/index.md), **ktElement**: KtElement?) : [Compactable](../-compactable/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [Entity](-entity.md)|  [jvm] ~~fun~~ [~~Entity~~](-entity.md)~~(~~~~name~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~className~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~signature~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~location~~~~:~~ [Location](../-location/index.md)~~,~~ ~~ktElement~~~~:~~ KtElement?~~)~~   <br>
| [Entity](-entity.md)|  [jvm] fun [Entity](-entity.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), location: [Location](../-location/index.md), ktElement: KtElement?)   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compact](compact.md)| [jvm]  <br>Brief description  <br><br><br>Contract to format implementing object to a string representation.<br><br>  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [compactWithSignature](../-compactable/compact-with-signature.md)| [jvm]  <br>Brief description  <br><br><br>Same as [compact](compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.<br><br>  <br>Content  <br>open override fun [compactWithSignature](../-compactable/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Location](../-location/index.md)  <br><br><br>
| [component4](component4.md)| [jvm]  <br>Content  <br>operator fun [component4](component4.md)(): KtElement?  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), location: [Location](../-location/index.md), ktElement: KtElement?): [Entity](index.md)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [ktElement](index.md#io.gitlab.arturbosch.detekt.api/Entity/ktElement/#/PointingToDeclaration/)|  [jvm] val [ktElement](index.md#io.gitlab.arturbosch.detekt.api/Entity/ktElement/#/PointingToDeclaration/): KtElement?   <br>
| [location](index.md#io.gitlab.arturbosch.detekt.api/Entity/location/#/PointingToDeclaration/)|  [jvm] val [location](index.md#io.gitlab.arturbosch.detekt.api/Entity/location/#/PointingToDeclaration/): [Location](../-location/index.md)   <br>
| [signature](index.md#io.gitlab.arturbosch.detekt.api/Entity/signature/#/PointingToDeclaration/)|  [jvm] val [signature](index.md#io.gitlab.arturbosch.detekt.api/Entity/signature/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>

