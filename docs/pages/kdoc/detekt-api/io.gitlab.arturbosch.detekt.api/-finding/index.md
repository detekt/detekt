---
title: Finding -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Finding](index.md)



# Finding  
 [jvm] 



Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces.



Basic behaviour of a finding is that is can be assigned to an id and a source code position described as an entity. Metrics and entity references can also considered for deeper characterization.



interface [Finding](index.md) : [Compactable](../-compactable/index.md), [HasEntity](../-has-entity/index.md), [HasMetrics](../-has-metrics/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compact](../-compactable/compact.md)| [jvm]  <br>Brief description  <br><br><br>Contract to format implementing object to a string representation.<br><br>  <br>Content  <br>abstract override fun [compact](../-compactable/compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [compactWithSignature](../-compactable/compact-with-signature.md)| [jvm]  <br>Brief description  <br><br><br>Same as compact except the content should contain a substring which represents this exact findings via a custom identifier.<br><br>  <br>Content  <br>open override fun [compactWithSignature](../-compactable/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [messageOrDescription](message-or-description.md)| [jvm]  <br>Brief description  <br><br><br>Explanation why this finding was raised.<br><br>  <br>Content  <br>abstract fun [messageOrDescription](message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [metricByType](../-has-metrics/metric-by-type.md)| [jvm]  <br>Brief description  <br><br><br>Finds the first metric matching given type.<br><br>  <br>Content  <br>open override fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [charPosition](index.md#io.gitlab.arturbosch.detekt.api/Finding/charPosition/#/PointingToDeclaration/)|  [jvm] open override val [charPosition](index.md#io.gitlab.arturbosch.detekt.api/Finding/charPosition/#/PointingToDeclaration/): [TextLocation](../-text-location/index.md)   <br>
| [entity](index.md#io.gitlab.arturbosch.detekt.api/Finding/entity/#/PointingToDeclaration/)|  [jvm] abstract override val [entity](index.md#io.gitlab.arturbosch.detekt.api/Finding/entity/#/PointingToDeclaration/): [Entity](../-entity/index.md)   <br>
| [file](index.md#io.gitlab.arturbosch.detekt.api/Finding/file/#/PointingToDeclaration/)|  [jvm] open override val [file](index.md#io.gitlab.arturbosch.detekt.api/Finding/file/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [id](index.md#io.gitlab.arturbosch.detekt.api/Finding/id/#/PointingToDeclaration/)|  [jvm] abstract val [id](index.md#io.gitlab.arturbosch.detekt.api/Finding/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [issue](index.md#io.gitlab.arturbosch.detekt.api/Finding/issue/#/PointingToDeclaration/)|  [jvm] abstract val [issue](index.md#io.gitlab.arturbosch.detekt.api/Finding/issue/#/PointingToDeclaration/): [Issue](../-issue/index.md)   <br>
| [location](index.md#io.gitlab.arturbosch.detekt.api/Finding/location/#/PointingToDeclaration/)|  [jvm] open override val [location](index.md#io.gitlab.arturbosch.detekt.api/Finding/location/#/PointingToDeclaration/): [Location](../-location/index.md)   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/Finding/message/#/PointingToDeclaration/)|  [jvm] abstract val [message](index.md#io.gitlab.arturbosch.detekt.api/Finding/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [metrics](index.md#io.gitlab.arturbosch.detekt.api/Finding/metrics/#/PointingToDeclaration/)|  [jvm] abstract override val [metrics](index.md#io.gitlab.arturbosch.detekt.api/Finding/metrics/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| [references](index.md#io.gitlab.arturbosch.detekt.api/Finding/references/#/PointingToDeclaration/)|  [jvm] abstract val [references](index.md#io.gitlab.arturbosch.detekt.api/Finding/references/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| [signature](index.md#io.gitlab.arturbosch.detekt.api/Finding/signature/#/PointingToDeclaration/)|  [jvm] open override val [signature](index.md#io.gitlab.arturbosch.detekt.api/Finding/signature/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [startPosition](index.md#io.gitlab.arturbosch.detekt.api/Finding/startPosition/#/PointingToDeclaration/)|  [jvm] open override val [startPosition](index.md#io.gitlab.arturbosch.detekt.api/Finding/startPosition/#/PointingToDeclaration/): [SourceLocation](../-source-location/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [CodeSmell](../-code-smell/index.md)

