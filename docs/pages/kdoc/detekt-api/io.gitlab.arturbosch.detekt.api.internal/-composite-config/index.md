---
title: CompositeConfig -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[CompositeConfig](index.md)



# CompositeConfig  
 [jvm] class [CompositeConfig](index.md)(**lookFirst**: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md), **lookSecond**: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)) : [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md), [ValidatableConfiguration](../-validatable-configuration/index.md)

Wraps two different configuration which should be considered when retrieving properties.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/CompositeConfig/#io.gitlab.arturbosch.detekt.api.Config#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[CompositeConfig](-composite-config.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/CompositeConfig/#io.gitlab.arturbosch.detekt.api.Config#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a> [jvm] fun [CompositeConfig](-composite-config.md)(lookFirst: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md), lookSecond: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/subConfig/#kotlin.String/PointingToDeclaration/"></a>[subConfig](sub-config.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/subConfig/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [subConfig](sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)  <br>More info  <br>Tries to retrieve part of the configuration based on given key.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/validate/#io.gitlab.arturbosch.detekt.api.Config#kotlin.collections.Set[kotlin.text.Regex]/PointingToDeclaration/"></a>[validate](validate.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/validate/#io.gitlab.arturbosch.detekt.api.Config#kotlin.collections.Set[kotlin.text.Regex]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [validate](validate.md)(baseline: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md), excludePatterns: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)>): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Notification](../../io.gitlab.arturbosch.detekt.api/-notification/index.md)>  <br>More info  <br>Validates both sides of the composite config according to defined properties of the baseline config.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[valueOrDefault](value-or-default.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun <[T](value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](value-or-default.md)): [T](value-or-default.md)  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[valueOrNull](value-or-null.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun <[T](value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](value-or-null.md)?  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/parentPath/#/PointingToDeclaration/"></a>[parentPath](parent-path.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig/parentPath/#/PointingToDeclaration/"></a> [jvm] open val [parentPath](parent-path.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?Keeps track of which key was taken to [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md) this configuration.   <br>

