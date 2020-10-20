---
title: ReportingExtension -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ReportingExtension](index.md)



# ReportingExtension  
 [jvm] interface [ReportingExtension](index.md) : [Extension](../-extension/index.md)

Allows to intercept detekt's result container by listening to the initial and final state and manipulate the reported findings.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[init](../-extension/init.md)| <a name="io.gitlab.arturbosch.detekt.api/Extension/init/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(config: [Config](../-config/index.md))  <br>More info  <br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension.  <br><br><br>[jvm]  <br>Content  <br>open fun [init](../-extension/init.md)(context: [SetupContext](../-setup-context/index.md))  <br>More info  <br>Setup extension by querying common paths and config options.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/onFinalResult/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[onFinalResult](on-final-result.md)| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/onFinalResult/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [onFinalResult](on-final-result.md)(result: [Detektion](../-detektion/index.md))  <br>More info  <br>Is called after all extensions's [transformFindings](transform-findings.md) were called.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/onRawResult/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[onRawResult](on-raw-result.md)| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/onRawResult/#io.gitlab.arturbosch.detekt.api.Detektion/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [onRawResult](on-raw-result.md)(result: [Detektion](../-detektion/index.md))  <br>More info  <br>Is called before any [transformFindings](transform-findings.md) calls were executed.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/transformFindings/#kotlin.collections.Map[kotlin.String,kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Finding]]/PointingToDeclaration/"></a>[transformFindings](transform-findings.md)| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/transformFindings/#kotlin.collections.Map[kotlin.String,kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Finding]]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [transformFindings](transform-findings.md)(findings: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>  <br>More info  <br>Allows to transform the reported findings e.g.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/id/#/PointingToDeclaration/"></a> [jvm] open val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Name of the extension.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/priority/#/PointingToDeclaration/"></a>[priority](priority.md)| <a name="io.gitlab.arturbosch.detekt.api/ReportingExtension/priority/#/PointingToDeclaration/"></a> [jvm] open val [priority](priority.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)Is used to run extensions in a specific order.   <br>

