---
title: OutputReport -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[OutputReport](index.md)



# OutputReport  
 [jvm] 

Translates detekt's result container - [Detektion](../-detektion/index.md) - into an output report which is written inside a file.

abstract class [OutputReport](index.md) : [Extension](../-extension/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [OutputReport](-output-report.md)|  [jvm] fun [OutputReport](-output-report.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [init](../-extension/init.md)| [jvm]  <br>Brief description  <br><br><br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Setup extension by querying common paths and config options.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br><br><br>
| [render](render.md)| [jvm]  <br>Brief description  <br><br><br>Defines the translation process of detekt's result into a string.<br><br>  <br>Content  <br>abstract fun [render](render.md)(detektion: [Detektion](../-detektion/index.md)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [write](write.md)| [jvm]  <br>Brief description  <br><br><br>Renders result and writes it to the given filePath.<br><br>  <br>Content  <br>fun [write](write.md)(filePath: [Path](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html), detektion: [Detektion](../-detektion/index.md))  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [ending](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/ending/#/PointingToDeclaration/)|  [jvm] <br><br>Supported ending of this report type.<br><br>abstract val [ending](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/ending/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [id](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/id/#/PointingToDeclaration/)|  [jvm] <br><br>Name of the extension.<br><br>open override val [id](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [name](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/name/#/PointingToDeclaration/)|  [jvm] <br><br>Name of the report. Is used to exclude this report in the yaml config.<br><br>open val [name](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/name/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>
| [priority](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/priority/#/PointingToDeclaration/)|  [jvm] <br><br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle.<br><br>open override val [priority](index.md#io.gitlab.arturbosch.detekt.api/OutputReport/priority/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

