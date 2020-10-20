---
title: HasEntity -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[HasEntity](index.md)



# HasEntity  
 [jvm] interface [HasEntity](index.md)

Describes a source code position.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/charPosition/#/PointingToDeclaration/"></a>[charPosition](char-position.md)| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/charPosition/#/PointingToDeclaration/"></a> [jvm] open val [charPosition](char-position.md): [TextLocation](../-text-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/entity/#/PointingToDeclaration/"></a>[entity](entity.md)| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/entity/#/PointingToDeclaration/"></a> [jvm] abstract val [entity](entity.md): [Entity](../-entity/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/file/#/PointingToDeclaration/"></a>[file](file.md)| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/file/#/PointingToDeclaration/"></a> [jvm] open val [file](file.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/location/#/PointingToDeclaration/"></a>[location](location.md)| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/location/#/PointingToDeclaration/"></a> [jvm] open val [location](location.md): [Location](../-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/signature/#/PointingToDeclaration/"></a>[signature](signature.md)| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/signature/#/PointingToDeclaration/"></a> [jvm] open val [signature](signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/startPosition/#/PointingToDeclaration/"></a>[startPosition](start-position.md)| <a name="io.gitlab.arturbosch.detekt.api/HasEntity/startPosition/#/PointingToDeclaration/"></a> [jvm] open val [startPosition](start-position.md): [SourceLocation](../-source-location/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api/Finding///PointingToDeclaration/"></a>[Finding](../-finding/index.md)

