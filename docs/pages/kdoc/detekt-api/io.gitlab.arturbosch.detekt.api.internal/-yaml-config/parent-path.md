---
title: parentPath -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[YamlConfig](index.md)/[parentPath](parent-path.md)



# parentPath  
[jvm]  
Content  
open override val [parentPath](parent-path.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)? = null  
More info  


Keeps track of which key was taken to [subConfig](sub-config.md) this configuration. Sub-sequential calls to [subConfig](sub-config.md) are tracked with '>' as a separator.



May be null if this is the top most configuration object.

  



