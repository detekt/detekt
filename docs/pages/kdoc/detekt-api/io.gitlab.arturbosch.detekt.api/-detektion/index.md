---
title: Detektion -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Detektion](index.md)



# Detektion  
 [jvm] interface [Detektion](index.md)

Storage for all kinds of findings and additional information which needs to be transferred from the detekt engine to the user.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Detektion/add/#io.gitlab.arturbosch.detekt.api.Notification/PointingToDeclaration/"></a>[add](add.md)| <a name="io.gitlab.arturbosch.detekt.api/Detektion/add/#io.gitlab.arturbosch.detekt.api.Notification/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [add](add.md)(notification: [Notification](../-notification/index.md))  <br>More info  <br>Stores a notification in the result.  <br><br><br>[jvm]  <br>Content  <br>abstract fun [add](add.md)(projectMetric: [ProjectMetric](../-project-metric/index.md))  <br>More info  <br>Stores a metric calculated for the whole project in the result.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Detektion/addData/#org.jetbrains.kotlin.com.intellij.openapi.util.Key[TypeParam(bounds=[kotlin.Any?])]#TypeParam(bounds=[kotlin.Any?])/PointingToDeclaration/"></a>[addData](add-data.md)| <a name="io.gitlab.arturbosch.detekt.api/Detektion/addData/#org.jetbrains.kotlin.com.intellij.openapi.util.Key[TypeParam(bounds=[kotlin.Any?])]#TypeParam(bounds=[kotlin.Any?])/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun <[V](add-data.md)> [addData](add-data.md)(key: Key<[V](add-data.md)>, value: [V](add-data.md))  <br>More info  <br>Stores an arbitrary value inside the result binded to the given key.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Detektion/getData/#org.jetbrains.kotlin.com.intellij.openapi.util.Key[TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[getData](get-data.md)| <a name="io.gitlab.arturbosch.detekt.api/Detektion/getData/#org.jetbrains.kotlin.com.intellij.openapi.util.Key[TypeParam(bounds=[kotlin.Any?])]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun <[V](get-data.md)> [getData](get-data.md)(key: Key<[V](get-data.md)>): [V](get-data.md)?  <br>More info  <br>Retrieves a value stored by the given key of the result.  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Detektion/findings/#/PointingToDeclaration/"></a>[findings](findings.md)| <a name="io.gitlab.arturbosch.detekt.api/Detektion/findings/#/PointingToDeclaration/"></a> [jvm] abstract val [findings](findings.md): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Detektion/metrics/#/PointingToDeclaration/"></a>[metrics](metrics.md)| <a name="io.gitlab.arturbosch.detekt.api/Detektion/metrics/#/PointingToDeclaration/"></a> [jvm] abstract val [metrics](metrics.md): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[ProjectMetric](../-project-metric/index.md)>   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Detektion/notifications/#/PointingToDeclaration/"></a>[notifications](notifications.md)| <a name="io.gitlab.arturbosch.detekt.api/Detektion/notifications/#/PointingToDeclaration/"></a> [jvm] abstract val [notifications](notifications.md): [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)<[Notification](../-notification/index.md)>   <br>

