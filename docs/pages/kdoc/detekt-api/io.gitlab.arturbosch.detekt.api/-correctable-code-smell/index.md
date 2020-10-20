---
title: CorrectableCodeSmell -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[CorrectableCodeSmell](index.md)



# CorrectableCodeSmell  
 [jvm] open class [CorrectableCodeSmell](index.md)(**issue**: [Issue](../-issue/index.md), **entity**: [Entity](../-entity/index.md), **message**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **metrics**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>, **references**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>, **autoCorrectEnabled**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) : [CodeSmell](../-code-smell/index.md)

Represents a code smell for that can be auto corrected.

   


## See also  
  
jvm  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell///PointingToDeclaration/"></a>[CodeSmell](../-code-smell/index.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell///PointingToDeclaration/"></a>
  


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/CorrectableCodeSmell/#io.gitlab.arturbosch.detekt.api.Issue#io.gitlab.arturbosch.detekt.api.Entity#kotlin.String#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Metric]#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Entity]#kotlin.Boolean/PointingToDeclaration/"></a>[CorrectableCodeSmell](-correctable-code-smell.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/CorrectableCodeSmell/#io.gitlab.arturbosch.detekt.api.Issue#io.gitlab.arturbosch.detekt.api.Entity#kotlin.String#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Metric]#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.Entity]#kotlin.Boolean/PointingToDeclaration/"></a> [jvm] fun [CorrectableCodeSmell](-correctable-code-smell.md)(issue: [Issue](../-issue/index.md), entity: [Entity](../-entity/index.md), message: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), metrics: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)> = listOf(), references: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)> = listOf(), autoCorrectEnabled: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/compact/#/PointingToDeclaration/"></a>[compact](../-code-smell/compact.md)| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/compact/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [compact](../-code-smell/compact.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Contract to format implementing object to a string representation.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/compactWithSignature/#/PointingToDeclaration/"></a>[compactWithSignature](../-code-smell/compact-with-signature.md)| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/compactWithSignature/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [compactWithSignature](../-code-smell/compact-with-signature.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Same as [compact](../-code-smell/compact.md) except the content should contain a substring which represents this exact findings via a custom identifier.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/messageOrDescription/#/PointingToDeclaration/"></a>[messageOrDescription](../-code-smell/message-or-description.md)| <a name="io.gitlab.arturbosch.detekt.api/CodeSmell/messageOrDescription/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [messageOrDescription](../-code-smell/message-or-description.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br>More info  <br>Explanation why this finding was raised.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/HasMetrics/metricByType/#kotlin.String/PointingToDeclaration/"></a>[metricByType](../-has-metrics/metric-by-type.md)| <a name="io.gitlab.arturbosch.detekt.api/HasMetrics/metricByType/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [metricByType](../-has-metrics/metric-by-type.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Metric](../-metric/index.md)?  <br>More info  <br>Finds the first metric matching given type.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/autoCorrectEnabled/#/PointingToDeclaration/"></a>[autoCorrectEnabled](auto-correct-enabled.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/autoCorrectEnabled/#/PointingToDeclaration/"></a> [jvm] val [autoCorrectEnabled](auto-correct-enabled.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/charPosition/#/PointingToDeclaration/"></a>[charPosition](char-position.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/charPosition/#/PointingToDeclaration/"></a> [jvm] open val [charPosition](char-position.md): [TextLocation](../-text-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/entity/#/PointingToDeclaration/"></a>[entity](entity.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/entity/#/PointingToDeclaration/"></a> [jvm] open override val [entity](entity.md): [Entity](../-entity/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/file/#/PointingToDeclaration/"></a>[file](file.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/file/#/PointingToDeclaration/"></a> [jvm] open val [file](file.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/id/#/PointingToDeclaration/"></a> [jvm] open override val [id](id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/issue/#/PointingToDeclaration/"></a>[issue](issue.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/issue/#/PointingToDeclaration/"></a> [jvm] override val [issue](issue.md): [Issue](../-issue/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/location/#/PointingToDeclaration/"></a>[location](location.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/location/#/PointingToDeclaration/"></a> [jvm] open val [location](location.md): [Location](../-location/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/message/#/PointingToDeclaration/"></a>[message](message.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/message/#/PointingToDeclaration/"></a> [jvm] open override val [message](message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/metrics/#/PointingToDeclaration/"></a>[metrics](metrics.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/metrics/#/PointingToDeclaration/"></a> [jvm] open override val [metrics](metrics.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Metric](../-metric/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/references/#/PointingToDeclaration/"></a>[references](references.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/references/#/PointingToDeclaration/"></a> [jvm] open override val [references](references.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Entity](../-entity/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/signature/#/PointingToDeclaration/"></a>[signature](signature.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/signature/#/PointingToDeclaration/"></a> [jvm] open val [signature](signature.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/startPosition/#/PointingToDeclaration/"></a>[startPosition](start-position.md)| <a name="io.gitlab.arturbosch.detekt.api/CorrectableCodeSmell/startPosition/#/PointingToDeclaration/"></a> [jvm] open val [startPosition](start-position.md): [SourceLocation](../-source-location/index.md)   <br>

