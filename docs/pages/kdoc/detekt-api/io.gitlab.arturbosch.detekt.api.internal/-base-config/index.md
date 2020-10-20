---
title: BaseConfig -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[BaseConfig](index.md)



# BaseConfig  
 [jvm] abstract class [BaseConfig](index.md) : [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)

Convenient base configuration which parses/casts the configuration value based on the type of the default value.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/BaseConfig/BaseConfig/#/PointingToDeclaration/"></a>[BaseConfig](-base-config.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/BaseConfig/BaseConfig/#/PointingToDeclaration/"></a> [jvm] fun [BaseConfig](-base-config.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config/subConfig/#kotlin.String/PointingToDeclaration/"></a>[subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/subConfig/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)  <br>More info  <br>Tries to retrieve part of the configuration based on given key.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[valueOrDefault](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun <[T](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md)): [T](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md)  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[valueOrNull](../../io.gitlab.arturbosch.detekt.api/-config/value-or-null.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun <[T](../../io.gitlab.arturbosch.detekt.api/-config/value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](../../io.gitlab.arturbosch.detekt.api/-config/value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](../../io.gitlab.arturbosch.detekt.api/-config/value-or-null.md)?  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/BaseConfig/parentPath/#/PointingToDeclaration/"></a>[parentPath](parent-path.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/BaseConfig/parentPath/#/PointingToDeclaration/"></a> [jvm] open val [parentPath](parent-path.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?Keeps track of which key was taken to [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md) this configuration.   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/YamlConfig///PointingToDeclaration/"></a>[YamlConfig](../-yaml-config/index.md)

