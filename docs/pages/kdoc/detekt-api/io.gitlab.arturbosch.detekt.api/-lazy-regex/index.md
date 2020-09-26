---
title: LazyRegex -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[LazyRegex](index.md)



# LazyRegex  
 [jvm] 



LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules. It computes the value once when reaching the point of its usage and returns the same value when requested again.



key & default are used to retrieve a value from config.



class [LazyRegex](index.md)(**key**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **default**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)) : [ReadOnlyProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/index.html)<[Rule](../-rule/index.md), [Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)>    


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [LazyRegex](-lazy-regex.md)|  [jvm] fun [LazyRegex](-lazy-regex.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [getValue](get-value.md)| [jvm]  <br>Content  <br>open operator override fun [getValue](get-value.md)(thisRef: [Rule](../-rule/index.md), property: [KProperty](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)<*>): [Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

