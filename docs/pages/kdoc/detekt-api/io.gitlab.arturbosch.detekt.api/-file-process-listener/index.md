---
title: FileProcessListener -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[FileProcessListener](index.md)



# FileProcessListener  
 [jvm] 



Gather additional metrics about the analyzed kotlin file. Pay attention to the thread policy of each function!



A bindingContext != BindingContext.EMPTY is only available if Kotlin compiler settings are used.



interface [FileProcessListener](index.md) : [Extension](../-extension/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [init](../-extension/init.md)| [jvm]  <br>Brief description  <br><br><br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Setup extension by querying common paths and config options.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br><br><br>
| [onFinish](on-finish.md)| [jvm]  <br>Brief description  <br><br><br><br><br>Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container. Do not do heavy computations here as this method is called from the main thread.<br><br><br><br>This method is called before any [ReportingExtension](../-reporting-extension/index.md).<br><br><br><br>  <br>Content  <br>~~open~~ ~~fun~~ [~~onFinish~~](on-finish.md)~~(~~~~files~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>~~,~~ ~~result~~~~:~~ [Detektion](../-detektion/index.md)~~)~~  <br>open fun [onFinish](on-finish.md)(files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>, result: [Detektion](../-detektion/index.md), bindingContext: BindingContext)  <br><br><br>
| [onProcess](on-process.md)| [jvm]  <br>Brief description  <br><br><br>Called when processing of a file begins. This method is called from a thread pool thread. Heavy computations allowed.<br><br>  <br>Content  <br>~~open~~ ~~fun~~ [~~onProcess~~](on-process.md)~~(~~~~file~~~~:~~ KtFile~~)~~  <br>open fun [onProcess](on-process.md)(file: KtFile, bindingContext: BindingContext)  <br><br><br>
| [onProcessComplete](on-process-complete.md)| [jvm]  <br>Brief description  <br><br><br>Called when processing of a file completes. This method is called from a thread pool thread. Heavy computations allowed.<br><br>  <br>Content  <br>~~open~~ ~~fun~~ [~~onProcessComplete~~](on-process-complete.md)~~(~~~~file~~~~:~~ KtFile~~,~~ ~~findings~~~~:~~ [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>~~)~~  <br>open fun [onProcessComplete](on-process-complete.md)(file: KtFile, findings: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>, bindingContext: BindingContext)  <br><br><br>
| [onStart](on-start.md)| [jvm]  <br>Brief description  <br><br><br>Use this to gather some additional information for the real onProcess function. This calculation should be lightweight as this method is called from the main thread.<br><br>  <br>Content  <br>~~open~~ ~~fun~~ [~~onStart~~](on-start.md)~~(~~~~files~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>~~)~~  <br>open fun [onStart](on-start.md)(files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>, bindingContext: BindingContext)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [id](index.md#io.gitlab.arturbosch.detekt.api/FileProcessListener/id/#/PointingToDeclaration/)|  [jvm] <br><br>Name of the extension.<br><br>open override val [id](index.md#io.gitlab.arturbosch.detekt.api/FileProcessListener/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [priority](index.md#io.gitlab.arturbosch.detekt.api/FileProcessListener/priority/#/PointingToDeclaration/)|  [jvm] <br><br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle.<br><br>open override val [priority](index.md#io.gitlab.arturbosch.detekt.api/FileProcessListener/priority/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

