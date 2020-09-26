---
title: RuleSetProvider -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[RuleSetProvider](index.md)



# RuleSetProvider  
 [jvm] 



A rule set provider, as the name states, is responsible for creating rule sets.



When writing own rule set providers make sure to register them according the ServiceLoader documentation. http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html



interface [RuleSetProvider](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [instance](instance.md)| [jvm]  <br>Brief description  <br><br><br>This function must be implemented to provide custom rule sets. Make sure to pass the configuration to each rule to allow rules to be self configurable.<br><br>  <br>Content  <br>abstract fun [instance](instance.md)(config: [Config](../-config/index.md)): [RuleSet](../-rule-set/index.md)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [ruleSetId](index.md#io.gitlab.arturbosch.detekt.api/RuleSetProvider/ruleSetId/#/PointingToDeclaration/)|  [jvm] <br><br>Every rule set must be pre-configured with an ID to validate if this rule set must be created for current analysis.<br><br>abstract val [ruleSetId](index.md#io.gitlab.arturbosch.detekt.api/RuleSetProvider/ruleSetId/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>


## Inheritors  
  
|  Name| 
|---|
| [DefaultRuleSetProvider](../../io.gitlab.arturbosch.detekt.api.internal/-default-rule-set-provider/index.md)

