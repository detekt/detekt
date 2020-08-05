---
title: onFinish -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[FileProcessListener](index.md)/[onFinish](on-finish.md)



# onFinish  
[jvm]  
Brief description  




Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container. Do not do heavy computations here as this method is called from the main thread.



This method is called before any [ReportingExtension](../-reporting-extension/index.md).



  
Content  
~~open~~ ~~fun~~ [~~onFinish~~](on-finish.md)~~(~~~~files~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>~~,~~ ~~result~~~~:~~ [Detektion](../-detektion/index.md)~~)~~  
open fun [onFinish](on-finish.md)(files: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtFile>, result: [Detektion](../-detektion/index.md), bindingContext: BindingContext)  



