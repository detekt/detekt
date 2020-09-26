---
title: YamlConfig -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[YamlConfig](index.md)



# YamlConfig  
 [jvm] 

Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification.

class [YamlConfig](index.md) : [BaseConfig](../-base-config/index.md), [ValidatableConfiguration](../-validatable-configuration/index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [keySequence](index.md#io.gitlab.arturbosch.detekt.api.internal/BaseConfig/keySequence/#kotlin.String/PointingToDeclaration/)| [jvm]  <br>Content  <br>override fun [keySequence](index.md#io.gitlab.arturbosch.detekt.api.internal/BaseConfig/keySequence/#kotlin.String/PointingToDeclaration/)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [subConfig](sub-config.md)| [jvm]  <br>Brief description  <br><br><br>Tries to retrieve part of the configuration based on given key.<br><br>  <br>Content  <br>open override fun [subConfig](sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [validate](validate.md)| [jvm]  <br>Content  <br>open override fun [validate](validate.md)(baseline: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md), excludePatterns: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[Regex](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)>): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Notification](../../io.gitlab.arturbosch.detekt.api/-notification/index.md)>  <br><br><br>
| [valueOrDefault](value-or-default.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.<br><br>  <br>Content  <br>open override fun <[T](value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](value-or-default.md)): [T](value-or-default.md)  <br><br><br>
| [valueOrNull](value-or-null.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.<br><br>  <br>Content  <br>open override fun <[T](value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](value-or-null.md)?  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [parentPath](index.md#io.gitlab.arturbosch.detekt.api.internal/YamlConfig/parentPath/#/PointingToDeclaration/)|  [jvm] <br><br><br><br>Keeps track of which key was taken to [subConfig](sub-config.md) this configuration. Sub-sequential calls to [subConfig](sub-config.md) are tracked with '>' as a separator.<br><br><br><br>May be null if this is the top most configuration object.<br><br><br><br>open override val [parentPath](index.md#io.gitlab.arturbosch.detekt.api.internal/YamlConfig/parentPath/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>
| [properties](index.md#io.gitlab.arturbosch.detekt.api.internal/YamlConfig/properties/#/PointingToDeclaration/)|  [jvm] val [properties](index.md#io.gitlab.arturbosch.detekt.api.internal/YamlConfig/properties/#/PointingToDeclaration/): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)>   <br>

