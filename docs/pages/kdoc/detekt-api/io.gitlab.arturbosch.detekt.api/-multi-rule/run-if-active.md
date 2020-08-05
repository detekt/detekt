---
title: runIfActive -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[MultiRule](index.md)/[runIfActive](run-if-active.md)



# runIfActive  
[jvm]  
Brief description  


Preferred way to run child rules because this composite rule takes care of evaluating if a specific child should be run at all.

  
Content  
fun <[T](run-if-active.md) : [Rule](../-rule/index.md)> [T](run-if-active.md).[runIfActive](run-if-active.md)(block: [T](run-if-active.md).() -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  



