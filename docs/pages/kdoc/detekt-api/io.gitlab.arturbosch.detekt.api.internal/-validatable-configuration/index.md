---
title: ValidatableConfiguration -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[ValidatableConfiguration](index.md)



# ValidatableConfiguration  
 [jvm] interface [ValidatableConfiguration](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [validate](validate.md)| [jvm]  <br>Content  <br>abstract fun [validate](validate.md)(baseline: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md), excludePatterns: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)>): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Notification](../../io.gitlab.arturbosch.detekt.api/-notification/index.md)>  <br><br><br>


## Inheritors  
  
|  Name| 
|---|
| [CompositeConfig](../-composite-config/index.md)
| [DisabledAutoCorrectConfig](../-disabled-auto-correct-config/index.md)
| [FailFastConfig](../-fail-fast-config/index.md)
| [YamlConfig](../-yaml-config/index.md)

