---
title: ConfigValidator -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ConfigValidator](index.md)



# ConfigValidator  
 [jvm] 



An extension which allows users to validate parts of the configuration.



Rule authors can validate if specific properties do appear in their config or if their value lies in a specified range.



interface [ConfigValidator](index.md) : [Extension](../-extension/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [init](../-extension/init.md)| [jvm]  <br>Brief description  <br><br><br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Setup extension by querying common paths and config options.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [validate](validate.md)| [jvm]  <br>Brief description  <br><br><br>Executes queries on given config and reports any warnings or errors via [Notification](../-notification/index.md)s.<br><br>  <br>Content  <br>abstract fun [validate](validate.md)(config: [Config](../-config/index.md)): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[Notification](../-notification/index.md)>  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [id](index.md#io.gitlab.arturbosch.detekt.api/ConfigValidator/id/#/PointingToDeclaration/)|  [jvm] <br><br>Name of the extension.<br><br>open override val [id](index.md#io.gitlab.arturbosch.detekt.api/ConfigValidator/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [priority](index.md#io.gitlab.arturbosch.detekt.api/ConfigValidator/priority/#/PointingToDeclaration/)|  [jvm] <br><br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle.<br><br>open override val [priority](index.md#io.gitlab.arturbosch.detekt.api/ConfigValidator/priority/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

