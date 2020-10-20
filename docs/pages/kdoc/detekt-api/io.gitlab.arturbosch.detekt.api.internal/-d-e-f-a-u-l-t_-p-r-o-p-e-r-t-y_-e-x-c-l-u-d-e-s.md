---
title: DEFAULT_PROPERTY_EXCLUDES -
---
//[detekt-api](../index.md)/[io.gitlab.arturbosch.detekt.api.internal](index.md)/[DEFAULT_PROPERTY_EXCLUDES](-d-e-f-a-u-l-t_-p-r-o-p-e-r-t-y_-e-x-c-l-u-d-e-s.md)



# DEFAULT_PROPERTY_EXCLUDES  
[jvm]  
Content  
val [DEFAULT_PROPERTY_EXCLUDES](-d-e-f-a-u-l-t_-p-r-o-p-e-r-t-y_-e-x-c-l-u-d-e-s.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  
More info  


Known existing properties on rule's which my be absent in the default-detekt-config.yml.



We need to predefine them as the user may not have already declared an 'config'-block in the configuration and we want to validate the config by default.

  



