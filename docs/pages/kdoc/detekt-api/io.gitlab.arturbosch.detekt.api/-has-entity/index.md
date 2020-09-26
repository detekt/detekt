---
title: HasEntity -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[HasEntity](index.md)



# HasEntity  
 [jvm] 

Describes a source code position.

interface [HasEntity](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [charPosition](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/charPosition/#/PointingToDeclaration/)|  [jvm] open val [charPosition](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/charPosition/#/PointingToDeclaration/): [TextLocation](../-text-location/index.md)   <br>
| [entity](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/entity/#/PointingToDeclaration/)|  [jvm] abstract val [entity](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/entity/#/PointingToDeclaration/): [Entity](../-entity/index.md)   <br>
| [file](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/file/#/PointingToDeclaration/)|  [jvm] open val [file](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/file/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [location](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/location/#/PointingToDeclaration/)|  [jvm] open val [location](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/location/#/PointingToDeclaration/): [Location](../-location/index.md)   <br>
| [signature](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/signature/#/PointingToDeclaration/)|  [jvm] open val [signature](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/signature/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [startPosition](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/startPosition/#/PointingToDeclaration/)|  [jvm] open val [startPosition](index.md#io.gitlab.arturbosch.detekt.api/HasEntity/startPosition/#/PointingToDeclaration/): [SourceLocation](../-source-location/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [Finding](../-finding/index.md)

