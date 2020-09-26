---
title: SetupContext -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[SetupContext](index.md)



# SetupContext  
 [jvm] 

Context providing useful processing settings to initialize extensions.

interface [SetupContext](index.md) : [PropertiesAware](../-properties-aware/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [register](../-properties-aware/register.md)| [jvm]  <br>Brief description  <br><br><br>Binds a given value with given key and stores it for later use.<br><br>  <br>Content  <br>abstract override fun [register](../-properties-aware/register.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [config](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/config/#/PointingToDeclaration/)|  [jvm] <br><br>Configuration which is used to setup detekt.<br><br>abstract val [config](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/config/#/PointingToDeclaration/): [Config](../-config/index.md)   <br>
| [configUris](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/configUris/#/PointingToDeclaration/)|  [jvm] <br><br>All config locations which where used to create [config](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/config/#/PointingToDeclaration/).<br><br>abstract val [configUris](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/configUris/#/PointingToDeclaration/): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[URI](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html)>   <br>
| [errorChannel](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/errorChannel/#/PointingToDeclaration/)|  [jvm] <br><br>The channel to log all the errors.<br><br>abstract val [errorChannel](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/errorChannel/#/PointingToDeclaration/): [Appendable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-appendable/index.html)   <br>
| [outputChannel](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/outputChannel/#/PointingToDeclaration/)|  [jvm] <br><br>The channel to log all the output.<br><br>abstract val [outputChannel](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/outputChannel/#/PointingToDeclaration/): [Appendable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-appendable/index.html)   <br>
| [properties](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/properties/#/PointingToDeclaration/)|  [jvm] <br><br>Raw properties.<br><br>abstract override val [properties](index.md#io.gitlab.arturbosch.detekt.api/SetupContext/properties/#/PointingToDeclaration/): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?>   <br>

