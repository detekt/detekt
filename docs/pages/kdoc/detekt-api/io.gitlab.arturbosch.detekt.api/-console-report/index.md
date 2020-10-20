---
title: ConsoleReport -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ConsoleReport](index.md)



# ConsoleReport  
 [jvm] abstract class [ConsoleReport](index.md) : [Extension](../-extension/index.md)

Extension point which describes how findings should be printed on the console.



Additional [ConsoleReport](index.md)'s can be made available through the [java.util.ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) pattern. If the default reporting mechanism should be turned off, exclude the entry 'FindingsReport' in the 'console-reports' property of a detekt yaml config.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/ConsoleReport/#/PointingToDeclaration/"></a>[ConsoleReport](-console-report.md)| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/ConsoleReport/#/PointingToDeclaration/"></a> [jvm] fun [ConsoleReport](-console-report.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[init](../-extension/init.md)| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br>More info  <br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.  <br><br><br>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br>More info  <br>Setup extension by querying common paths and config options.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/print/#java.io.PrintStream#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[print](print.md)| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/print/#java.io.PrintStream#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~fun~~ [~~print~~](print.md)~~(~~~~printer~~~~:~~ [PrintStream](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html)~~,~~ ~~detektion~~~~:~~ [Detektion](../-detektion/index.md)~~)~~  <br>More info  <br>Prints the rendered report to the given printer if anything was rendered at all.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/render/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[render](render.md)| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/render/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [render](render.md)(detektion: [Detektion](../-detektion/index.md)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?  <br>More info  <br>Converts the given detektion into a string representation to present it to the client.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/id/#/PointingToDeclaration/"></a> [jvm] open val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Name of the extension.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/priority/#/PointingToDeclaration/"></a>[priority](priority.md)| <a name="io.gitlab.arturbosch.detekt.api/ConsoleReport/priority/#/PointingToDeclaration/"></a> [jvm] open val [priority](priority.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Is used to run extensions in a specific order.   <br>

