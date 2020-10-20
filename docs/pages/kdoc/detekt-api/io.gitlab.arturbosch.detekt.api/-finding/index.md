---
title: Finding -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Finding](index.md)



# Finding  
 [jvm] interface [Finding](index.md) : [Compactable](../-compactable/index.md), [HasEntity](../-has-entity/index.md), [HasMetrics](../-has-metrics/index.md)

Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces.



Basic behaviour of a finding is that is can be assigned to an id and a source code position described as an entity. Metrics and entity references can also considered for deeper characterization.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compact/#/PointingToDeclaration/"></a>[compact](../-compactable/compact.md)| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compact/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [compact](../-compactable/compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Contract to format implementing object to a string representation.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compactWithSignature/#/PointingToDeclaration/"></a>[compactWithSignature](../-compactable/compact-with-signature.md)| <a name="io.gitlab.arturbosch.detekt.api/Compactable/compactWithSignature/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [compactWithSignature](../-compactable/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Same as [compact](../-compactable/compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/messageOrDescription/#/PointingToDeclaration/"></a>[messageOrDescription](message-or-description.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/messageOrDescription/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [messageOrDescription](message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Explanation why this finding was raised.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/HasMetrics/metricByType/#kotlin.String/PointingToDeclaration/"></a>[metricByType](../-has-metrics/metric-by-type.md)| <a name="io.gitlab.arturbosch.detekt.api/HasMetrics/metricByType/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br>More info  <br>Finds the first metric matching given type.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Finding/charPosition/#/PointingToDeclaration/"></a>[charPosition](char-position.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/charPosition/#/PointingToDeclaration/"></a> [jvm] open val [charPosition](char-position.md): [TextLocation](../-text-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/entity/#/PointingToDeclaration/"></a>[entity](entity.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/entity/#/PointingToDeclaration/"></a> [jvm] abstract val [entity](entity.md): [Entity](../-entity/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/file/#/PointingToDeclaration/"></a>[file](file.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/file/#/PointingToDeclaration/"></a> [jvm] open val [file](file.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/id/#/PointingToDeclaration/"></a> [jvm] abstract val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/issue/#/PointingToDeclaration/"></a>[issue](issue.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/issue/#/PointingToDeclaration/"></a> [jvm] abstract val [issue](issue.md): [Issue](../-issue/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/location/#/PointingToDeclaration/"></a>[location](location.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/location/#/PointingToDeclaration/"></a> [jvm] open val [location](location.md): [Location](../-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/message/#/PointingToDeclaration/"></a>[message](message.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/message/#/PointingToDeclaration/"></a> [jvm] abstract val [message](message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/metrics/#/PointingToDeclaration/"></a>[metrics](metrics.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/metrics/#/PointingToDeclaration/"></a> [jvm] abstract val [metrics](metrics.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/references/#/PointingToDeclaration/"></a>[references](references.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/references/#/PointingToDeclaration/"></a> [jvm] abstract val [references](references.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/signature/#/PointingToDeclaration/"></a>[signature](signature.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/signature/#/PointingToDeclaration/"></a> [jvm] open val [signature](signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Finding/startPosition/#/PointingToDeclaration/"></a>[startPosition](start-position.md)| <a name="io.gitlab.arturbosch.detekt.api/Finding/startPosition/#/PointingToDeclaration/"></a> [jvm] open val [startPosition](start-position.md): [SourceLocation](../-source-location/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell///PointingToDeclaration/"></a>[CodeSmell](../-code-smell/index.md)

