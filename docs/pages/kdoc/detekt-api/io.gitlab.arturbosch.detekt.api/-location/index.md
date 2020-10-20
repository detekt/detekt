---
title: Location -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Location](index.md)



# Location  
 [jvm] data class [Location](index.md)(**source**: [SourceLocation](../-source-location/index.md), **text**: [TextLocation](../-text-location/index.md), **file**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [Compactable](../-compactable/index.md)

Specifies a position within a source code fragment.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Location/Location/#io.gitlab.arturbosch.detekt.api.SourceLocation#io.gitlab.arturbosch.detekt.api.TextLocation#kotlin.String#kotlin.String/PointingToDeclaration/"></a>[Location](-location.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/Location/#io.gitlab.arturbosch.detekt.api.SourceLocation#io.gitlab.arturbosch.detekt.api.TextLocation#kotlin.String#kotlin.String/PointingToDeclaration/"></a> [jvm] ~~fun~~ [~~Location~~](-location.md)~~(~~~~source~~~~:~~ [SourceLocation](../-source-location/index.md)~~,~~ ~~text~~~~:~~ [TextLocation](../-text-location/index.md)~~,~~ ~~locationString~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~,~~ ~~file~~~~:~~ [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)~~)~~   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/Location/#io.gitlab.arturbosch.detekt.api.SourceLocation#io.gitlab.arturbosch.detekt.api.TextLocation#kotlin.String/PointingToDeclaration/"></a>[Location](-location.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/Location/#io.gitlab.arturbosch.detekt.api.SourceLocation#io.gitlab.arturbosch.detekt.api.TextLocation#kotlin.String/PointingToDeclaration/"></a> [jvm] fun [Location](-location.md)(source: [SourceLocation](../-source-location/index.md), text: [TextLocation](../-text-location/index.md), file: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))   <br>


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Location.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Location.Companion///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Location/compact/#/PointingToDeclaration/"></a>[compact](compact.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/compact/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Contract to format implementing object to a string representation.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compactWithSignature/#/PointingToDeclaration/"></a>[compactWithSignature](../-compactable/compact-with-signature.md)| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compactWithSignature/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [compactWithSignature](../-compactable/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Same as [compact](../-compactable/compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/component1/#/PointingToDeclaration/"></a>[component1](component1.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/component1/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [SourceLocation](../-source-location/index.md)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/component2/#/PointingToDeclaration/"></a>[component2](component2.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/component2/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [TextLocation](../-text-location/index.md)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/component3/#/PointingToDeclaration/"></a>[component3](component3.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/component3/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/copy/#io.gitlab.arturbosch.detekt.api.SourceLocation#io.gitlab.arturbosch.detekt.api.TextLocation#kotlin.String/PointingToDeclaration/"></a>[copy](copy.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/copy/#io.gitlab.arturbosch.detekt.api.SourceLocation#io.gitlab.arturbosch.detekt.api.TextLocation#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [copy](copy.md)(source: [SourceLocation](../-source-location/index.md), text: [TextLocation](../-text-location/index.md), file: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Location](index.md)  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Location/file/#/PointingToDeclaration/"></a>[file](file.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/file/#/PointingToDeclaration/"></a> [jvm] val [file](file.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/source/#/PointingToDeclaration/"></a>[source](source.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/source/#/PointingToDeclaration/"></a> [jvm] val [source](source.md): [SourceLocation](../-source-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Location/text/#/PointingToDeclaration/"></a>[text](text.md)| <a name="io.gitlab.arturbosch.detekt.api/Location/text/#/PointingToDeclaration/"></a> [jvm] val [text](text.md): [TextLocation](../-text-location/index.md)   <br>

