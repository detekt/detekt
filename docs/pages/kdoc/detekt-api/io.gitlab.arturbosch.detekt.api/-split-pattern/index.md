---
title: SplitPattern -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[SplitPattern](index.md)



# SplitPattern  
 [jvm] open class [SplitPattern](index.md)(**text**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **delimiters**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **removeTrailingAsterisks**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))

Splits given text into parts and provides testing utilities for its elements. Basic use cases are to specify different function or class names in the detekt yaml config and test for their appearance in specific rules.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/SplitPattern/#kotlin.String#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a>[SplitPattern](-split-pattern.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/SplitPattern/#kotlin.String#kotlin.String#kotlin.Boolean/PointingToDeclaration/"></a> [jvm] fun [SplitPattern](-split-pattern.md)(text: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), delimiters: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = ",", removeTrailingAsterisks: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/any/#kotlin.String?/PointingToDeclaration/"></a>[any](any.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/any/#kotlin.String?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [any](any.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Is there any element which matches the given value?  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/contains/#kotlin.String?/PointingToDeclaration/"></a>[contains](contains.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/contains/#kotlin.String?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [contains](contains.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Does any part contain given value?  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/mapAll/#kotlin.Function1[kotlin.String,TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[mapAll](map-all.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/mapAll/#kotlin.Function1[kotlin.String,TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun <[T](map-all.md)> [mapAll](map-all.md)(transform: ([String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) -> [T](map-all.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[T](map-all.md)>  <br>More info  <br>Transforms all parts by given transform function.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/matches/#kotlin.String/PointingToDeclaration/"></a>[matches](matches.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/matches/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [matches](matches.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  <br>More info  <br>Finds all parts which match the given value.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/none/#kotlin.String/PointingToDeclaration/"></a>[none](none.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/none/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [none](none.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Tests if none of the parts contain the given value.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/startWith/#kotlin.String?/PointingToDeclaration/"></a>[startWith](start-with.md)| <a name="io.gitlab.arturbosch.detekt.api/SplitPattern/startWith/#kotlin.String?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [startWith](start-with.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Tests if any part starts with the given value  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/CommaSeparatedPattern///PointingToDeclaration/"></a>[CommaSeparatedPattern](../../io.gitlab.arturbosch.detekt.api.internal/-comma-separated-pattern/index.md)

