---
title: LazyRegex -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[LazyRegex](index.md)



# LazyRegex  
 [jvm] class [LazyRegex](index.md)(**key**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **default**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [ReadOnlyProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/index.html)<[Rule](../-rule/index.md), [Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)> 

LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules. It computes the value once when reaching the point of its usage and returns the same value when requested again.



key & default are used to retrieve a value from config.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/LazyRegex/LazyRegex/#kotlin.String#kotlin.String/PointingToDeclaration/"></a>[LazyRegex](-lazy-regex.md)| <a name="io.gitlab.arturbosch.detekt.api/LazyRegex/LazyRegex/#kotlin.String#kotlin.String/PointingToDeclaration/"></a> [jvm] fun [LazyRegex](-lazy-regex.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/LazyRegex/getValue/#io.gitlab.arturbosch.detekt.api.Rule#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[getValue](get-value.md)| <a name="io.gitlab.arturbosch.detekt.api/LazyRegex/getValue/#io.gitlab.arturbosch.detekt.api.Rule#kotlin.reflect.KProperty[*]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [getValue](get-value.md)(thisRef: [Rule](../-rule/index.md), property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

