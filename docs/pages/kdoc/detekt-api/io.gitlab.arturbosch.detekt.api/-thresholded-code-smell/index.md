---
title: ThresholdedCodeSmell -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ThresholdedCodeSmell](index.md)



# ThresholdedCodeSmell  
 [jvm] open class [ThresholdedCodeSmell](index.md)(**issue**: [Issue](../-issue/index.md), **entity**: [Entity](../-entity/index.md), **metric**: [Metric](../-metric/index.md), **message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **references**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>) : [CodeSmell](../-code-smell/index.md)

Represents a code smell for which a specific metric can be determined which is responsible for the existence of this rule violation.

   


## See also  
  
jvm  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell///PointingToDeclaration/"></a>[CodeSmell](../-code-smell/index.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell///PointingToDeclaration/"></a>
  


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/ThresholdedCodeSmell/#io.gitlab.arturbosch.detekt.api.Issue#io.gitlab.arturbosch.detekt.api.Entity#io.gitlab.arturbosch.detekt.api.Metric#kotlin.String#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Entity]/PointingToDeclaration/"></a>[ThresholdedCodeSmell](-thresholded-code-smell.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/ThresholdedCodeSmell/#io.gitlab.arturbosch.detekt.api.Issue#io.gitlab.arturbosch.detekt.api.Entity#io.gitlab.arturbosch.detekt.api.Metric#kotlin.String#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Entity]/PointingToDeclaration/"></a> [jvm] fun [ThresholdedCodeSmell](-thresholded-code-smell.md)(issue: [Issue](../-issue/index.md), entity: [Entity](../-entity/index.md), metric: [Metric](../-metric/index.md), message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), references: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)> = emptyList())   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/compact/#/PointingToDeclaration/"></a>[compact](compact.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/compact/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Contract to format implementing object to a string representation.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/compactWithSignature/#/PointingToDeclaration/"></a>[compactWithSignature](../-code-smell/compact-with-signature.md)| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/compactWithSignature/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [compactWithSignature](../-code-smell/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Same as [compact](../-code-smell/compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/messageOrDescription/#/PointingToDeclaration/"></a>[messageOrDescription](message-or-description.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/messageOrDescription/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [messageOrDescription](message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Explanation why this finding was raised.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/HasMetrics/metricByType/#kotlin.String/PointingToDeclaration/"></a>[metricByType](../-has-metrics/metric-by-type.md)| <a name="io.gitlab.arturbosch.detekt.api/HasMetrics/metricByType/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br>More info  <br>Finds the first metric matching given type.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/toString/#/PointingToDeclaration/"></a>[toString](../-code-smell/to-string.md)| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](../-code-smell/to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/charPosition/#/PointingToDeclaration/"></a>[charPosition](char-position.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/charPosition/#/PointingToDeclaration/"></a> [jvm] open val [charPosition](char-position.md): [TextLocation](../-text-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/entity/#/PointingToDeclaration/"></a>[entity](entity.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/entity/#/PointingToDeclaration/"></a> [jvm] open override val [entity](entity.md): [Entity](../-entity/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/file/#/PointingToDeclaration/"></a>[file](file.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/file/#/PointingToDeclaration/"></a> [jvm] open val [file](file.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/id/#/PointingToDeclaration/"></a> [jvm] open override val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/issue/#/PointingToDeclaration/"></a>[issue](issue.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/issue/#/PointingToDeclaration/"></a> [jvm] override val [issue](issue.md): [Issue](../-issue/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/location/#/PointingToDeclaration/"></a>[location](location.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/location/#/PointingToDeclaration/"></a> [jvm] open val [location](location.md): [Location](../-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/message/#/PointingToDeclaration/"></a>[message](message.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/message/#/PointingToDeclaration/"></a> [jvm] open override val [message](message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metric/#/PointingToDeclaration/"></a>[metric](metric.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metric/#/PointingToDeclaration/"></a> [jvm] val [metric](metric.md): [Metric](../-metric/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metrics/#/PointingToDeclaration/"></a>[metrics](metrics.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metrics/#/PointingToDeclaration/"></a> [jvm] open override val [metrics](metrics.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/references/#/PointingToDeclaration/"></a>[references](references.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/references/#/PointingToDeclaration/"></a> [jvm] open override val [references](references.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/signature/#/PointingToDeclaration/"></a>[signature](signature.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/signature/#/PointingToDeclaration/"></a> [jvm] open val [signature](signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/startPosition/#/PointingToDeclaration/"></a>[startPosition](start-position.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/startPosition/#/PointingToDeclaration/"></a> [jvm] open val [startPosition](start-position.md): [SourceLocation](../-source-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/threshold/#/PointingToDeclaration/"></a>[threshold](threshold.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/threshold/#/PointingToDeclaration/"></a> [jvm] val [threshold](threshold.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/value/#/PointingToDeclaration/"></a>[value](value.md)| <a name="io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/value/#/PointingToDeclaration/"></a> [jvm] val [value](value.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

