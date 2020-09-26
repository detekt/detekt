---
title: ThresholdedCodeSmell -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ThresholdedCodeSmell](index.md)



# ThresholdedCodeSmell  
 [jvm] 

Represents a code smell for which a specific metric can be determined which is responsible for the existence of this rule violation.

open class [ThresholdedCodeSmell](index.md)(**issue**: [Issue](../-issue/index.md), **entity**: [Entity](../-entity/index.md), **metric**: [Metric](../-metric/index.md), **message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **references**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>) : [CodeSmell](../-code-smell/index.md)   


## See also  
  
jvm  
  
|  Name|  Summary| 
|---|---|
| [CodeSmell](../-code-smell/index.md)| <br><br><br><br>
  


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [ThresholdedCodeSmell](-thresholded-code-smell.md)|  [jvm] fun [ThresholdedCodeSmell](-thresholded-code-smell.md)(issue: [Issue](../-issue/index.md), entity: [Entity](../-entity/index.md), metric: [Metric](../-metric/index.md), message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), references: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compact](compact.md)| [jvm]  <br>Brief description  <br><br><br>Contract to format implementing object to a string representation.<br><br>  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [compactWithSignature](../-code-smell/compact-with-signature.md)| [jvm]  <br>Brief description  <br><br><br>Same as [compact](compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.<br><br>  <br>Content  <br>open override fun [compactWithSignature](../-code-smell/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [messageOrDescription](message-or-description.md)| [jvm]  <br>Brief description  <br><br><br>Explanation why this finding was raised.<br><br>  <br>Content  <br>open override fun [messageOrDescription](message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [metricByType](../-has-metrics/metric-by-type.md)| [jvm]  <br>Brief description  <br><br><br>Finds the first metric matching given type.<br><br>  <br>Content  <br>open override fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br><br><br>
| [toString](../-code-smell/to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](../-code-smell/to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [charPosition](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/charPosition/#/PointingToDeclaration/)|  [jvm] open override val [charPosition](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/charPosition/#/PointingToDeclaration/): [TextLocation](../-text-location/index.md)   <br>
| [entity](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/entity/#/PointingToDeclaration/)|  [jvm] open override val [entity](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/entity/#/PointingToDeclaration/): [Entity](../-entity/index.md)   <br>
| [file](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/file/#/PointingToDeclaration/)|  [jvm] open override val [file](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/file/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [id](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/id/#/PointingToDeclaration/)|  [jvm] open override val [id](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [issue](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/issue/#/PointingToDeclaration/)|  [jvm] override val [issue](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/issue/#/PointingToDeclaration/): [Issue](../-issue/index.md)   <br>
| [location](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/location/#/PointingToDeclaration/)|  [jvm] open override val [location](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/location/#/PointingToDeclaration/): [Location](../-location/index.md)   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/message/#/PointingToDeclaration/)|  [jvm] open override val [message](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [metric](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metric/#/PointingToDeclaration/)|  [jvm] val [metric](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metric/#/PointingToDeclaration/): [Metric](../-metric/index.md)   <br>
| [metrics](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metrics/#/PointingToDeclaration/)|  [jvm] open override val [metrics](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/metrics/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| [references](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/references/#/PointingToDeclaration/)|  [jvm] open override val [references](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/references/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| [signature](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/signature/#/PointingToDeclaration/)|  [jvm] open override val [signature](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/signature/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [startPosition](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/startPosition/#/PointingToDeclaration/)|  [jvm] open override val [startPosition](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/startPosition/#/PointingToDeclaration/): [SourceLocation](../-source-location/index.md)   <br>
| [threshold](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/threshold/#/PointingToDeclaration/)|  [jvm] val [threshold](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/threshold/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [value](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/value/#/PointingToDeclaration/)|  [jvm] val [value](index.md#io.gitlab.arturbosch.detekt.api/ThresholdedCodeSmell/value/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

