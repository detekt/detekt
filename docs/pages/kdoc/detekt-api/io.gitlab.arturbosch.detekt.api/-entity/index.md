---
title: Entity -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Entity](index.md)



# Entity  
 [jvm] data class [Entity](index.md)(**name**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **signature**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **location**: [Location](../-location/index.md), **ktElement**: KtElement?) : [Compactable](../-compactable/index.md)

Stores information about a specific code fragment.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Entity/Entity/#kotlin.String#kotlin.String#kotlin.String#io.gitlab.arturbosch.detekt.api.Location#org.jetbrains.kotlin.psi.KtElement?/PointingToDeclaration/"></a>[Entity](-entity.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/Entity/#kotlin.String#kotlin.String#kotlin.String#io.gitlab.arturbosch.detekt.api.Location#org.jetbrains.kotlin.psi.KtElement?/PointingToDeclaration/"></a> [jvm] ~~fun~~ [~~Entity~~](-entity.md)~~(~~~~name~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~className~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~signature~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~location~~~~:~~ [Location](../-location/index.md)~~,~~ ~~ktElement~~~~:~~ KtElement? ~~= null~~~~)~~   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/Entity/#kotlin.String#kotlin.String#io.gitlab.arturbosch.detekt.api.Location#org.jetbrains.kotlin.psi.KtElement?/PointingToDeclaration/"></a>[Entity](-entity.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/Entity/#kotlin.String#kotlin.String#io.gitlab.arturbosch.detekt.api.Location#org.jetbrains.kotlin.psi.KtElement?/PointingToDeclaration/"></a> [jvm] fun [Entity](-entity.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), location: [Location](../-location/index.md), ktElement: KtElement? = null)   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Entity.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity.Companion///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Entity/compact/#/PointingToDeclaration/"></a>[compact](compact.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/compact/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Contract to format implementing object to a string representation.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compactWithSignature/#/PointingToDeclaration/"></a>[compactWithSignature](../-compactable/compact-with-signature.md)| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compactWithSignature/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [compactWithSignature](../-compactable/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Same as [compact](../-compactable/compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/component2/#/PointingToDeclaration/"></a>[component2](component2.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/component2/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/component3/#/PointingToDeclaration/"></a>[component3](component3.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/component3/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Location](../-location/index.md)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/component4/#/PointingToDeclaration/"></a>[component4](component4.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/component4/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component4](component4.md)(): KtElement?  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/copy/#kotlin.String#kotlin.String#io.gitlab.arturbosch.detekt.api.Location#org.jetbrains.kotlin.psi.KtElement?/PointingToDeclaration/"></a>[copy](copy.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/copy/#kotlin.String#kotlin.String#io.gitlab.arturbosch.detekt.api.Location#org.jetbrains.kotlin.psi.KtElement?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [copy](copy.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), signature: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), location: [Location](../-location/index.md), ktElement: KtElement? = null): [Entity](index.md)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Entity/ktElement/#/PointingToDeclaration/"></a>[ktElement](kt-element.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/ktElement/#/PointingToDeclaration/"></a> [jvm] val [ktElement](kt-element.md): KtElement? = null   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/location/#/PointingToDeclaration/"></a>[location](location.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/location/#/PointingToDeclaration/"></a> [jvm] val [location](location.md): [Location](../-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Entity/signature/#/PointingToDeclaration/"></a>[signature](signature.md)| <a name="io.gitlab.arturbosch.detekt.api/Entity/signature/#/PointingToDeclaration/"></a> [jvm] val [signature](signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>

