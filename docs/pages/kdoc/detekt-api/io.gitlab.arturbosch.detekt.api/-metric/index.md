---
title: Metric -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Metric](index.md)



# Metric  
 [jvm] data class [Metric](index.md)(**type**: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), **value**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **threshold**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), **isDouble**: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), **conversionFactor**: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))

Metric type, can be an integer or double value. Internally it is stored as an integer, but the conversion factor and is double attributes can be used to retrieve it as a double value.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Metric/Metric/#kotlin.String#kotlin.Double#kotlin.Double#kotlin.Int/PointingToDeclaration/"></a>[Metric](-metric.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/Metric/#kotlin.String#kotlin.Double#kotlin.Double#kotlin.Int/PointingToDeclaration/"></a> [jvm] fun [Metric](-metric.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), threshold: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), conversionFactor: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = DEFAULT_FLOAT_CONVERSION_FACTOR)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/Metric/#kotlin.String#kotlin.Int#kotlin.Int#kotlin.Boolean#kotlin.Int/PointingToDeclaration/"></a>[Metric](-metric.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/Metric/#kotlin.String#kotlin.Int#kotlin.Int#kotlin.Boolean#kotlin.Int/PointingToDeclaration/"></a> [jvm] fun [Metric](-metric.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), threshold: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), isDouble: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, conversionFactor: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = DEFAULT_FLOAT_CONVERSION_FACTOR)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Metric/component1/#/PointingToDeclaration/"></a>[component1](component1.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/component1/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component1](component1.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/component2/#/PointingToDeclaration/"></a>[component2](component2.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/component2/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component2](component2.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/component3/#/PointingToDeclaration/"></a>[component3](component3.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/component3/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component3](component3.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/component4/#/PointingToDeclaration/"></a>[component4](component4.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/component4/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component4](component4.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/component5/#/PointingToDeclaration/"></a>[component5](component5.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/component5/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator fun [component5](component5.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/copy/#kotlin.String#kotlin.Int#kotlin.Int#kotlin.Boolean#kotlin.Int/PointingToDeclaration/"></a>[copy](copy.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/copy/#kotlin.String#kotlin.Int#kotlin.Int#kotlin.Boolean#kotlin.Int/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [copy](copy.md)(type: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), threshold: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), isDouble: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false, conversionFactor: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = DEFAULT_FLOAT_CONVERSION_FACTOR): [Metric](index.md)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/doubleThreshold/#/PointingToDeclaration/"></a>[doubleThreshold](double-threshold.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/doubleThreshold/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [doubleThreshold](double-threshold.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>More info  <br>Specified threshold for this metric as a double value.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/doubleValue/#/PointingToDeclaration/"></a>[doubleValue](double-value.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/doubleValue/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [doubleValue](double-value.md)(): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)  <br>More info  <br>Convenient method to retrieve the raised value as a double.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/toString/#/PointingToDeclaration/"></a>[toString](to-string.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Metric/conversionFactor/#/PointingToDeclaration/"></a>[conversionFactor](conversion-factor.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/conversionFactor/#/PointingToDeclaration/"></a> [jvm] val [conversionFactor](conversion-factor.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/isDouble/#/PointingToDeclaration/"></a>[isDouble](is-double.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/isDouble/#/PointingToDeclaration/"></a> [jvm] val [isDouble](is-double.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = false   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/threshold/#/PointingToDeclaration/"></a>[threshold](threshold.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/threshold/#/PointingToDeclaration/"></a> [jvm] val [threshold](threshold.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/type/#/PointingToDeclaration/"></a>[type](type.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/type/#/PointingToDeclaration/"></a> [jvm] val [type](type.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Metric/value/#/PointingToDeclaration/"></a>[value](value.md)| <a name="io.gitlab.arturbosch.detekt.api/Metric/value/#/PointingToDeclaration/"></a> [jvm] val [value](value.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

