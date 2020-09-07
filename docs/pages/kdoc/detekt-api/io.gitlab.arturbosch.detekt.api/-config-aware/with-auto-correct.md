---
title: withAutoCorrect -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ConfigAware](index.md)/[withAutoCorrect](with-auto-correct.md)



# withAutoCorrect  
[jvm]  
Brief description  


If your rule supports to automatically correct the misbehaviour of underlying smell, specify your code inside this method call, to allow the user of your rule to trigger auto correction only when needed.

  
Content  
open fun [withAutoCorrect](with-auto-correct.md)(block: () -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  



