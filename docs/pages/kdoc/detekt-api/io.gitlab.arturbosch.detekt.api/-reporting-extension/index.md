---
title: ReportingExtension -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ReportingExtension](index.md)



# ReportingExtension  
 [jvm] 

Allows to intercept detekt's result container by listening to the initial and final state and manipulate the reported findings.

interface [ReportingExtension](index.md) : [Extension](../-extension/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [init](../-extension/init.md)| [jvm]  <br>Brief description  <br><br><br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Setup extension by querying common paths and config options.<br><br>  <br>Content  <br>open override fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br><br><br>
| [onFinalResult](on-final-result.md)| [jvm]  <br>Brief description  <br><br><br>Is called after all extensions's [transformFindings](transform-findings.md) were called.<br><br>  <br>Content  <br>open fun [onFinalResult](on-final-result.md)(result: [Detektion](../-detektion/index.md))  <br><br><br>
| [onRawResult](on-raw-result.md)| [jvm]  <br>Brief description  <br><br><br>Is called before any [transformFindings](transform-findings.md) calls were executed.<br><br>  <br>Content  <br>open fun [onRawResult](on-raw-result.md)(result: [Detektion](../-detektion/index.md))  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [transformFindings](transform-findings.md)| [jvm]  <br>Brief description  <br><br><br>Allows to transform the reported findings e.g. apply custom filtering.<br><br>  <br>Content  <br>open fun [transformFindings](transform-findings.md)(findings: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [id](index.md#io.gitlab.arturbosch.detekt.api/ReportingExtension/id/#/PointingToDeclaration/)|  [jvm] <br><br>Name of the extension.<br><br>open override val [id](index.md#io.gitlab.arturbosch.detekt.api/ReportingExtension/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [priority](index.md#io.gitlab.arturbosch.detekt.api/ReportingExtension/priority/#/PointingToDeclaration/)|  [jvm] <br><br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle.<br><br>open override val [priority](index.md#io.gitlab.arturbosch.detekt.api/ReportingExtension/priority/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

