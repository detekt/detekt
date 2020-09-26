---
title: BaseConfig -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[BaseConfig](index.md)



# BaseConfig  
 [jvm] 

Convenient base configuration which parses/casts the configuration value based on the type of the default value.

abstract class [BaseConfig](index.md) : [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [BaseConfig](-base-config.md)|  [jvm] fun [BaseConfig](-base-config.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md)| [jvm]  <br>Brief description  <br><br><br>Tries to retrieve part of the configuration based on given key.<br><br>  <br>Content  <br>abstract override fun [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)  <br><br><br>
| [toString](../-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [valueOrDefault](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.<br><br>  <br>Content  <br>open override fun <T : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](../../io.gitlab.arturbosch.detekt.api/-config/value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: T): T  <br><br><br>
| [valueOrNull](../../io.gitlab.arturbosch.detekt.api/-config/value-or-null.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.<br><br>  <br>Content  <br>abstract override fun <T : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](../../io.gitlab.arturbosch.detekt.api/-config/value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): T?  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [parentPath](index.md#io.gitlab.arturbosch.detekt.api.internal/BaseConfig/parentPath/#/PointingToDeclaration/)|  [jvm] <br><br><br><br>Keeps track of which key was taken to subConfig this configuration. Sub-sequential calls to subConfig are tracked with '>' as a separator.<br><br><br><br>May be null if this is the top most configuration object.<br><br><br><br>open override val [parentPath](index.md#io.gitlab.arturbosch.detekt.api.internal/BaseConfig/parentPath/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>


## Inheritors  
  
|  Name| 
|---|
| [YamlConfig](../-yaml-config/index.md)

