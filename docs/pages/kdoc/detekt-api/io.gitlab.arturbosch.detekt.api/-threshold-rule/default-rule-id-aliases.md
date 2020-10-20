---
title: defaultRuleIdAliases -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ThresholdRule](index.md)/[defaultRuleIdAliases](default-rule-id-aliases.md)



# defaultRuleIdAliases  
[jvm]  
Content  
open val [defaultRuleIdAliases](default-rule-id-aliases.md): [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  
More info  


The default names which can be used instead of this #ruleId to refer to this rule in suppression's.



When overriding this property make sure to meet following structure for detekt-generator to pick it up and generate documentation for aliases:

    override val defaultRuleIdAliases = setOf("Name1", "Name2")  



