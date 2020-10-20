---
title: Notification -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Notification](index.md)



# Notification  
 [jvm] interface [Notification](index.md)

Any kind of notification which should be printed to the console. For example when using the formatting rule set, any change to your kotlin file is a notification.

   


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Notification.Level///PointingToDeclaration/"></a>[Level](-level/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Notification.Level///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>enum [Level](-level/index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)<[Notification.Level](-level/index.md)>   <br>More info  <br>Level of severity of the notification  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Notification/isError/#/PointingToDeclaration/"></a>[isError](is-error.md)| <a name="io.gitlab.arturbosch.detekt.api/Notification/isError/#/PointingToDeclaration/"></a> [jvm] open val [isError](is-error.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Notification/level/#/PointingToDeclaration/"></a>[level](level.md)| <a name="io.gitlab.arturbosch.detekt.api/Notification/level/#/PointingToDeclaration/"></a> [jvm] abstract val [level](level.md): [Notification.Level](-level/index.md)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Notification/message/#/PointingToDeclaration/"></a>[message](message.md)| <a name="io.gitlab.arturbosch.detekt.api/Notification/message/#/PointingToDeclaration/"></a> [jvm] abstract val [message](message.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/SimpleNotification///PointingToDeclaration/"></a>[SimpleNotification](../../io.gitlab.arturbosch.detekt.api.internal/-simple-notification/index.md)

