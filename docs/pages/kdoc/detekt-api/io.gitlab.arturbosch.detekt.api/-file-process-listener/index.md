---
title: FileProcessListener -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[FileProcessListener](index.md)



# FileProcessListener  
 [jvm] interface [FileProcessListener](index.md) : [Extension](../-extension/index.md)

Gather additional metrics about the analyzed kotlin file. Pay attention to the thread policy of each function!



A bindingContext != BindingContext.EMPTY is only available if Kotlin compiler settings are used.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[init](../-extension/init.md)| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br>More info  <br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.  <br><br><br>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br>More info  <br>Setup extension by querying common paths and config options.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onFinish/#kotlin.collections.List[org.jetbrains.kotlin.psi.KtFile]#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[onFinish](on-finish.md)| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onFinish/#kotlin.collections.List[org.jetbrains.kotlin.psi.KtFile]#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~open~~ ~~fun~~ [~~onFinish~~](on-finish.md)~~(~~~~files~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>~~,~~ ~~result~~~~:~~ [Detektion](../-detektion/index.md)~~)~~  <br>open fun [onFinish](on-finish.md)(files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>, result: [Detektion](../-detektion/index.md), bindingContext: BindingContext)  <br>More info  <br>Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onProcess/#org.jetbrains.kotlin.psi.KtFile/PointingToDeclaration/"></a>[onProcess](on-process.md)| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onProcess/#org.jetbrains.kotlin.psi.KtFile/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~open~~ ~~fun~~ [~~onProcess~~](on-process.md)~~(~~~~file~~~~:~~ KtFile~~)~~  <br>open fun [onProcess](on-process.md)(file: KtFile, bindingContext: BindingContext)  <br>More info  <br>Called when processing of a file begins.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onProcessComplete/#org.jetbrains.kotlin.psi.KtFile#kotlin.collections.Map[kotlin.String,kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Finding]]/PointingToDeclaration/"></a>[onProcessComplete](on-process-complete.md)| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onProcessComplete/#org.jetbrains.kotlin.psi.KtFile#kotlin.collections.Map[kotlin.String,kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Finding]]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~open~~ ~~fun~~ [~~onProcessComplete~~](on-process-complete.md)~~(~~~~file~~~~:~~ KtFile~~,~~ ~~findings~~~~:~~ [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>~~)~~  <br>open fun [onProcessComplete](on-process-complete.md)(file: KtFile, findings: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>, bindingContext: BindingContext)  <br>More info  <br>Called when processing of a file completes.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onStart/#kotlin.collections.List[org.jetbrains.kotlin.psi.KtFile]/PointingToDeclaration/"></a>[onStart](on-start.md)| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/onStart/#kotlin.collections.List[org.jetbrains.kotlin.psi.KtFile]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~open~~ ~~fun~~ [~~onStart~~](on-start.md)~~(~~~~files~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>~~)~~  <br>open fun [onStart](on-start.md)(files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>, bindingContext: BindingContext)  <br>More info  <br>Use this to gather some additional information for the real onProcess function.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/id/#/PointingToDeclaration/"></a> [jvm] open val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Name of the extension.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/priority/#/PointingToDeclaration/"></a>[priority](priority.md)| <a name="io.gitlab.arturbosch.detekt.api/FileProcessListener/priority/#/PointingToDeclaration/"></a> [jvm] open val [priority](priority.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Is used to run extensions in a specific order.   <br>

