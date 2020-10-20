---
title: load -
---
//[detekt-api](../../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../../index.md)/[YamlConfig](../index.md)/[Companion](index.md)/[load](load.md)



# load  
[jvm]  
Content  
fun [load](load.md)(path: [Path](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)): [Config](../../../io.gitlab.arturbosch.detekt.api/-config/index.md)  
More info  


Factory method to load a yaml configuration. Given path must exist and point to a readable file.

  


[jvm]  
Content  
fun [load](load.md)(reader: [Reader](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html)): [Config](../../../io.gitlab.arturbosch.detekt.api/-config/index.md)  
More info  


Constructs a [YamlConfig](../index.md) from any [Reader](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html).



Note the reader will be consumed and closed.

  



