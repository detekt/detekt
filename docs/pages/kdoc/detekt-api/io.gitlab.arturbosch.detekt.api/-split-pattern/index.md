---
title: SplitPattern -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[SplitPattern](index.md)



# SplitPattern  
 [jvm] 

Splits given text into parts and provides testing utilities for its elements. Basic use cases are to specify different function or class names in the detekt yaml config and test for their appearance in specific rules.

open class [SplitPattern](index.md)(**text**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **delimiters**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **removeTrailingAsterisks**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [SplitPattern](-split-pattern.md)|  [jvm] fun [SplitPattern](-split-pattern.md)(text: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), delimiters: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), removeTrailingAsterisks: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [any](any.md)| [jvm]  <br>Brief description  <br><br><br>Is there any element which matches the given value?<br><br>  <br>Content  <br>fun [any](any.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [contains](contains.md)| [jvm]  <br>Brief description  <br><br><br>Does any part contain given value?<br><br>  <br>Content  <br>fun [contains](contains.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [mapAll](map-all.md)| [jvm]  <br>Brief description  <br><br><br>Transforms all parts by given transform function.<br><br>  <br>Content  <br>fun <[T](map-all.md)> [mapAll](map-all.md)(transform: ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) -> [T](map-all.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[T](map-all.md)>  <br><br><br>
| [matches](matches.md)| [jvm]  <br>Brief description  <br><br><br>Finds all parts which match the given value.<br><br>  <br>Content  <br>fun [matches](matches.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  <br><br><br>
| [none](none.md)| [jvm]  <br>Brief description  <br><br><br>Tests if none of the parts contain the given value.<br><br>  <br>Content  <br>fun [none](none.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [startWith](start-with.md)| [jvm]  <br>Brief description  <br><br><br>Tests if any part starts with the given value<br><br>  <br>Content  <br>fun [startWith](start-with.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| [CommaSeparatedPattern](../../io.gitlab.arturbosch.detekt.api.internal/-comma-separated-pattern/index.md)

