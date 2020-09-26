---
title: Metric -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Metric](index.md)



# Metric  
 [jvm] 

Metric type, can be an integer or double value. Internally it is stored as an integer, but the conversion factor and is double attributes can be used to retrieve it as a double value.

data class [Metric](index.md)(**type**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **value**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **threshold**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **isDouble**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), **conversionFactor**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [Metric](-metric.md)|  [jvm] fun [Metric](-metric.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), threshold: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), conversionFactor: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   <br>
| [Metric](-metric.md)|  [jvm] fun [Metric](-metric.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), threshold: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), isDouble: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), conversionFactor: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component3](component3.md)| [jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [component4](component4.md)| [jvm]  <br>Content  <br>operator fun [component4](component4.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [component5](component5.md)| [jvm]  <br>Content  <br>operator fun [component5](component5.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), threshold: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), isDouble: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), conversionFactor: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Metric](index.md)  <br><br><br>
| [doubleThreshold](double-threshold.md)| [jvm]  <br>Brief description  <br><br><br>Specified threshold for this metric as a double value.<br><br>  <br>Content  <br>fun [doubleThreshold](double-threshold.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [doubleValue](double-value.md)| [jvm]  <br>Brief description  <br><br><br>Convenient method to retrieve the raised value as a double. Internally the value is stored as an int with a conversion factor to not loose any precision in calculations.<br><br>  <br>Content  <br>fun [doubleValue](double-value.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](to-string.md)| [jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [conversionFactor](index.md#io.gitlab.arturbosch.detekt.api/Metric/conversionFactor/#/PointingToDeclaration/)|  [jvm] val [conversionFactor](index.md#io.gitlab.arturbosch.detekt.api/Metric/conversionFactor/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [isDouble](index.md#io.gitlab.arturbosch.detekt.api/Metric/isDouble/#/PointingToDeclaration/)|  [jvm] val [isDouble](index.md#io.gitlab.arturbosch.detekt.api/Metric/isDouble/#/PointingToDeclaration/): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| [threshold](index.md#io.gitlab.arturbosch.detekt.api/Metric/threshold/#/PointingToDeclaration/)|  [jvm] val [threshold](index.md#io.gitlab.arturbosch.detekt.api/Metric/threshold/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| [type](index.md#io.gitlab.arturbosch.detekt.api/Metric/type/#/PointingToDeclaration/)|  [jvm] val [type](index.md#io.gitlab.arturbosch.detekt.api/Metric/type/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [value](index.md#io.gitlab.arturbosch.detekt.api/Metric/value/#/PointingToDeclaration/)|  [jvm] val [value](index.md#io.gitlab.arturbosch.detekt.api/Metric/value/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

