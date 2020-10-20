---
title: ConfigValidator -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ConfigValidator](index.md)



# ConfigValidator  
 [jvm] interface [ConfigValidator](index.md) : [Extension](../-extension/index.md)

An extension which allows users to validate parts of the configuration.



Rule authors can validate if specific properties do appear in their config or if their value lies in a specified range.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[init](../-extension/init.md)| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br>More info  <br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.  <br><br><br>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br>More info  <br>Setup extension by querying common paths and config options.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigValidator/validate/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[validate](validate.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigValidator/validate/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [validate](validate.md)(config: [Config](../-config/index.md)): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[Notification](../-notification/index.md)>  <br>More info  <br>Executes queries on given config and reports any warnings or errors via [Notification](../-notification/index.md)s.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ConfigValidator/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigValidator/id/#/PointingToDeclaration/"></a> [jvm] open val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Name of the extension.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigValidator/priority/#/PointingToDeclaration/"></a>[priority](priority.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigValidator/priority/#/PointingToDeclaration/"></a> [jvm] open val [priority](priority.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Is used to run extensions in a specific order.   <br>

