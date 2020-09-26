---
title: CodeSmell -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[CodeSmell](index.md)



# CodeSmell  
 [jvm] 



A code smell indicates any possible design problem inside a program's source code. The type of a code smell is described by an [Issue](../-issue/index.md).



If the design problem results from metric violations, a list of [Metric](../-metric/index.md)'s can describe further the kind of metrics.



If the design problem manifests by different source locations, references to these locations can be stored in additional [Entity](../-entity/index.md)'s.



open class [CodeSmell](index.md)(**issue**: [Issue](../-issue/index.md), **entity**: [Entity](../-entity/index.md), **message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **metrics**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>, **references**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>) : [Finding](../-finding/index.md)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [CodeSmell](-code-smell.md)|  [jvm] fun [CodeSmell](-code-smell.md)(issue: [Issue](../-issue/index.md), entity: [Entity](../-entity/index.md), message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), metrics: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>, references: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compact](compact.md)| [jvm]  <br>Brief description  <br><br><br>Contract to format implementing object to a string representation.<br><br>  <br>Content  <br>open override fun [compact](compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [compactWithSignature](compact-with-signature.md)| [jvm]  <br>Brief description  <br><br><br>Same as [compact](compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.<br><br>  <br>Content  <br>open override fun [compactWithSignature](compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [messageOrDescription](message-or-description.md)| [jvm]  <br>Brief description  <br><br><br>Explanation why this finding was raised.<br><br>  <br>Content  <br>open override fun [messageOrDescription](message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [metricByType](../-has-metrics/metric-by-type.md)| [jvm]  <br>Brief description  <br><br><br>Finds the first metric matching given type.<br><br>  <br>Content  <br>open override fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [charPosition](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/charPosition/#/PointingToDeclaration/)|  [jvm] open override val [charPosition](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/charPosition/#/PointingToDeclaration/): [TextLocation](../-text-location/index.md)   <br>
| [entity](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/entity/#/PointingToDeclaration/)|  [jvm] open override val [entity](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/entity/#/PointingToDeclaration/): [Entity](../-entity/index.md)   <br>
| [file](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/file/#/PointingToDeclaration/)|  [jvm] open override val [file](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/file/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [id](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/id/#/PointingToDeclaration/)|  [jvm] open override val [id](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [issue](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/issue/#/PointingToDeclaration/)|  [jvm] override val [issue](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/issue/#/PointingToDeclaration/): [Issue](../-issue/index.md)   <br>
| [location](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/location/#/PointingToDeclaration/)|  [jvm] open override val [location](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/location/#/PointingToDeclaration/): [Location](../-location/index.md)   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/message/#/PointingToDeclaration/)|  [jvm] open override val [message](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [metrics](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/metrics/#/PointingToDeclaration/)|  [jvm] open override val [metrics](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/metrics/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| [references](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/references/#/PointingToDeclaration/)|  [jvm] open override val [references](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/references/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| [signature](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/signature/#/PointingToDeclaration/)|  [jvm] open override val [signature](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/signature/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [startPosition](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/startPosition/#/PointingToDeclaration/)|  [jvm] open override val [startPosition](index.md#io.gitlab.arturbosch.detekt.api/CodeSmell/startPosition/#/PointingToDeclaration/): [SourceLocation](../-source-location/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [CorrectableCodeSmell](../-correctable-code-smell/index.md)
| [ThresholdedCodeSmell](../-thresholded-code-smell/index.md)

