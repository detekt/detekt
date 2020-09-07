---
title: Detektion -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Detektion](index.md)



# Detektion  
 [jvm] 

Storage for all kinds of findings and additional information which needs to be transferred from the detekt engine to the user.

interface [Detektion](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [add](add.md)| [jvm]  <br>Brief description  <br><br><br>Stores a notification in the result.<br><br>  <br>Content  <br>abstract fun [add](add.md)(notification: [Notification](../-notification/index.md))  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Stores a metric calculated for the whole project in the result.<br><br>  <br>Content  <br>abstract fun [add](add.md)(projectMetric: [ProjectMetric](../-project-metric/index.md))  <br><br><br>
| [addData](add-data.md)| [jvm]  <br>Brief description  <br><br><br>Stores an arbitrary value inside the result binded to the given key.<br><br>  <br>Content  <br>abstract fun <[V](add-data.md)> [addData](add-data.md)(key: Key<[V](add-data.md)>, value: [V](add-data.md))  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [getData](get-data.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a value stored by the given key of the result.<br><br>  <br>Content  <br>abstract fun <[V](get-data.md)> [getData](get-data.md)(key: Key<[V](get-data.md)>): [V](get-data.md)?  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [findings](index.md#io.gitlab.arturbosch.detekt.api/Detektion/findings/#/PointingToDeclaration/)|  [jvm] abstract val [findings](index.md#io.gitlab.arturbosch.detekt.api/Detektion/findings/#/PointingToDeclaration/): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>   <br>
| [metrics](index.md#io.gitlab.arturbosch.detekt.api/Detektion/metrics/#/PointingToDeclaration/)|  [jvm] abstract val [metrics](index.md#io.gitlab.arturbosch.detekt.api/Detektion/metrics/#/PointingToDeclaration/): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[ProjectMetric](../-project-metric/index.md)>   <br>
| [notifications](index.md#io.gitlab.arturbosch.detekt.api/Detektion/notifications/#/PointingToDeclaration/)|  [jvm] abstract val [notifications](index.md#io.gitlab.arturbosch.detekt.api/Detektion/notifications/#/PointingToDeclaration/): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[Notification](../-notification/index.md)>   <br>

