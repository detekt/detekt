---
title: Notification -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Notification](index.md)



# Notification  
 [jvm] 

Any kind of notification which should be printed to the console. For example when using the formatting rule set, any change to your kotlin file is a notification.

interface [Notification](index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| [Level](-level/index.md)| [jvm]  <br>Brief description  <br><br><br>Level of severity of the notification<br><br>  <br>Content  <br>enum [Level](-level/index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)<[Notification.Level](-level/index.md)>   <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [isError](index.md#io.gitlab.arturbosch.detekt.api/Notification/isError/#/PointingToDeclaration/)|  [jvm] open val [isError](index.md#io.gitlab.arturbosch.detekt.api/Notification/isError/#/PointingToDeclaration/): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| [level](index.md#io.gitlab.arturbosch.detekt.api/Notification/level/#/PointingToDeclaration/)|  [jvm] abstract val [level](index.md#io.gitlab.arturbosch.detekt.api/Notification/level/#/PointingToDeclaration/): [Notification.Level](-level/index.md)   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/Notification/message/#/PointingToDeclaration/)|  [jvm] abstract val [message](index.md#io.gitlab.arturbosch.detekt.api/Notification/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [SimpleNotification](../../io.gitlab.arturbosch.detekt.api.internal/-simple-notification/index.md)

