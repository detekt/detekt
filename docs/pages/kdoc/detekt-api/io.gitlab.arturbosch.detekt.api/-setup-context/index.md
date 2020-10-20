---
title: SetupContext -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[SetupContext](index.md)



# SetupContext  
 [jvm] interface [SetupContext](index.md) : [PropertiesAware](../-properties-aware/index.md)

Context providing useful processing settings to initialize extensions.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/PropertiesAware/register/#kotlin.String#kotlin.Any/PointingToDeclaration/"></a>[register](../-properties-aware/register.md)| <a name="io.gitlab.arturbosch.detekt.api/PropertiesAware/register/#kotlin.String#kotlin.Any/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [register](../-properties-aware/register.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))  <br>More info  <br>Binds a given value with given key and stores it for later use.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/config/#/PointingToDeclaration/"></a>[config](config.md)| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/config/#/PointingToDeclaration/"></a> [jvm] abstract val [config](config.md): [Config](../-config/index.md)Configuration which is used to setup detekt.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/configUris/#/PointingToDeclaration/"></a>[configUris](config-uris.md)| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/configUris/#/PointingToDeclaration/"></a> [jvm] abstract val [configUris](config-uris.md): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[URI](https://docs.oracle.com/javase/8/docs/api/java/net/URI.html)>All config locations which where used to create [config](config.md).   <br>
| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/errorChannel/#/PointingToDeclaration/"></a>[errorChannel](error-channel.md)| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/errorChannel/#/PointingToDeclaration/"></a> [jvm] abstract val [errorChannel](error-channel.md): [Appendable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-appendable/index.html)The channel to log all the errors.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/outputChannel/#/PointingToDeclaration/"></a>[outputChannel](output-channel.md)| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/outputChannel/#/PointingToDeclaration/"></a> [jvm] abstract val [outputChannel](output-channel.md): [Appendable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-appendable/index.html)The channel to log all the output.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/properties/#/PointingToDeclaration/"></a>[properties](properties.md)| <a name="io.gitlab.arturbosch.detekt.api/SetupContext/properties/#/PointingToDeclaration/"></a> [jvm] abstract val [properties](properties.md): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?>Raw properties.   <br>

