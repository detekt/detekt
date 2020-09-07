---
title: onStart -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[FileProcessListener](index.md)/[onStart](on-start.md)



# onStart  
[jvm]  
Brief description  


Use this to gather some additional information for the real onProcess function. This calculation should be lightweight as this method is called from the main thread.

  
Content  
~~open~~ ~~fun~~ [~~onStart~~](on-start.md)~~(~~~~files~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>~~)~~  
open fun [onStart](on-start.md)(files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>, bindingContext: BindingContext)  



