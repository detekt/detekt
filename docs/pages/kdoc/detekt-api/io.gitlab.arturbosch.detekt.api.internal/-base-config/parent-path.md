---
title: parentPath -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[BaseConfig](index.md)/[parentPath](parent-path.md)



# parentPath  
[jvm]  
Content  
open val [parentPath](parent-path.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?  
More info  


Keeps track of which key was taken to [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md) this configuration. Sub-sequential calls to [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.md) are tracked with '>' as a separator.



May be null if this is the top most configuration object.

  



