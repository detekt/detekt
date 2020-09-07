---
title: visitCondition -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[BaseRule](index.md)/[visitCondition](visit-condition.md)



# visitCondition  
[jvm]  
Brief description  




Basic mechanism to decide if a rule should run or not.



By default any rule which is declared 'active' in the [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md) or not suppressed by a [Suppress](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-suppress/index.html) annotation on file level should run.



  
Content  
abstract fun [visitCondition](visit-condition.md)(root: KtFile): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  



