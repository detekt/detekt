---
title: OutputReport -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[OutputReport](index.md)



# OutputReport  
 [jvm] abstract class [OutputReport](index.md) : [Extension](../-extension/index.md)

Translates detekt's result container - [Detektion](../-detektion/index.md) - into an output report which is written inside a file.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/OutputReport/#/PointingToDeclaration/"></a>[OutputReport](-output-report.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/OutputReport/#/PointingToDeclaration/"></a> [jvm] fun [OutputReport](-output-report.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[init](../-extension/init.md)| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br>More info  <br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.  <br><br><br>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br>More info  <br>Setup extension by querying common paths and config options.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/render/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[render](render.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/render/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [render](render.md)(detektion: [Detektion](../-detektion/index.md)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?  <br>More info  <br>Defines the translation process of detekt's result into a string.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/write/#java.nio.file.Path#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[write](write.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/write/#java.nio.file.Path#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [write](write.md)(filePath: [Path](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html), detektion: [Detektion](../-detektion/index.md))  <br>More info  <br>Renders result and writes it to the given filePath.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/ending/#/PointingToDeclaration/"></a>[ending](ending.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/ending/#/PointingToDeclaration/"></a> [jvm] abstract val [ending](ending.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Supported ending of this report type.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/id/#/PointingToDeclaration/"></a> [jvm] open val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Name of the extension.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/name/#/PointingToDeclaration/"></a>[name](name.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/name/#/PointingToDeclaration/"></a> [jvm] open val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?Name of the report.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/priority/#/PointingToDeclaration/"></a>[priority](priority.md)| <a name="io.gitlab.arturbosch.detekt.api/OutputReport/priority/#/PointingToDeclaration/"></a> [jvm] open val [priority](priority.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Is used to run extensions in a specific order.   <br>

