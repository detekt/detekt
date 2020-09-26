---
title: CorrectableCodeSmell -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[CorrectableCodeSmell](index.md)



# CorrectableCodeSmell  
 [jvm] 

Represents a code smell for that can be auto corrected.

open class [CorrectableCodeSmell](index.md)(**issue**: [Issue](../-issue/index.md), **entity**: [Entity](../-entity/index.md), **message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **metrics**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>, **references**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>, **autoCorrectEnabled**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) : [CodeSmell](../-code-smell/index.md)   


## See also  
  
jvm  
  
|  Name|  Summary| 
|---|---|
| [CodeSmell](../-code-smell/index.md)| <br><br><br><br>
  


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [CorrectableCodeSmell](-correctable-code-smell.md)|  [jvm] fun [CorrectableCodeSmell](-correctable-code-smell.md)(issue: [Issue](../-issue/index.md), entity: [Entity](../-entity/index.md), message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), metrics: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>, references: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>, autoCorrectEnabled: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compact](../-code-smell/compact.md)| [jvm]  <br>Brief description  <br><br><br>Contract to format implementing object to a string representation.<br><br>  <br>Content  <br>open override fun [compact](../-code-smell/compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [compactWithSignature](../-code-smell/compact-with-signature.md)| [jvm]  <br>Brief description  <br><br><br>Same as compact except the content should contain a substring which represents this exact findings via a custom identifier.<br><br>  <br>Content  <br>open override fun [compactWithSignature](../-code-smell/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [messageOrDescription](../-code-smell/message-or-description.md)| [jvm]  <br>Brief description  <br><br><br>Explanation why this finding was raised.<br><br>  <br>Content  <br>open override fun [messageOrDescription](../-code-smell/message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [metricByType](../-has-metrics/metric-by-type.md)| [jvm]  <br>Brief description  <br><br><br>Finds the first metric matching given type.<br><br>  <br>Content  <br>open override fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [autoCorrectEnabled](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/autoCorrectEnabled/#/PointingToDeclaration/)|  [jvm] val [autoCorrectEnabled](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/autoCorrectEnabled/#/PointingToDeclaration/): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| [charPosition](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/charPosition/#/PointingToDeclaration/)|  [jvm] open override val [charPosition](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/charPosition/#/PointingToDeclaration/): [TextLocation](../-text-location/index.md)   <br>
| [entity](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/entity/#/PointingToDeclaration/)|  [jvm] open override val [entity](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/entity/#/PointingToDeclaration/): [Entity](../-entity/index.md)   <br>
| [file](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/file/#/PointingToDeclaration/)|  [jvm] open override val [file](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/file/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [id](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/id/#/PointingToDeclaration/)|  [jvm] open override val [id](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/id/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [issue](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/issue/#/PointingToDeclaration/)|  [jvm] override val [issue](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/issue/#/PointingToDeclaration/): [Issue](../-issue/index.md)   <br>
| [location](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/location/#/PointingToDeclaration/)|  [jvm] open override val [location](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/location/#/PointingToDeclaration/): [Location](../-location/index.md)   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/message/#/PointingToDeclaration/)|  [jvm] open override val [message](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [metrics](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/metrics/#/PointingToDeclaration/)|  [jvm] open override val [metrics](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/metrics/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| [references](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/references/#/PointingToDeclaration/)|  [jvm] open override val [references](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/references/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| [signature](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/signature/#/PointingToDeclaration/)|  [jvm] open override val [signature](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/signature/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [startPosition](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/startPosition/#/PointingToDeclaration/)|  [jvm] open override val [startPosition](index.md#io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/startPosition/#/PointingToDeclaration/): [SourceLocation](../-source-location/index.md)   <br>

