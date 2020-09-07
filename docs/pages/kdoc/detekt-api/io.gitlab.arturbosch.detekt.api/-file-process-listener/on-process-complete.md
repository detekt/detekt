---
title: onProcessComplete -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[FileProcessListener](index.md)/[onProcessComplete](on-process-complete.md)



# onProcessComplete  
[jvm]  
Brief description  


Called when processing of a file completes. This method is called from a thread pool thread. Heavy computations allowed.

  
Content  
~~open~~ ~~fun~~ [~~onProcessComplete~~](on-process-complete.md)~~(~~~~file~~~~:~~ KtFile~~,~~ ~~findings~~~~:~~ [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>~~)~~  
open fun [onProcessComplete](on-process-complete.md)(file: KtFile, findings: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>>, bindingContext: BindingContext)  



