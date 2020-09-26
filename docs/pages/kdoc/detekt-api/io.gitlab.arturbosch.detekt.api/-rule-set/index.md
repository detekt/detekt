---
title: RuleSet -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[RuleSet](index.md)



# RuleSet  
 [jvm] 

A rule set is a collection of rules and must be defined within a rule set provider implementation.

class [RuleSet](index.md)(**id**: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/), **rules**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)>)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [RuleSet](-rule-set.md)|  [jvm] fun [RuleSet](-rule-set.md)(id: [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/), rules: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [accept](accept.md)| [jvm]  <br>Brief description  <br><br><br>Visits given file with all rules of this rule set, returning a list of all code smell findings.<br><br>  <br>Content  <br>~~fun~~ [~~accept~~](accept.md)~~(~~~~file~~~~:~~ KtFile~~,~~ ~~bindingContext~~~~:~~ BindingContext~~)~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>  <br><br><br>
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [id](index.md#io.gitlab.arturbosch.detekt.api/RuleSet/id/#/PointingToDeclaration/)|  [jvm] val [id](index.md#io.gitlab.arturbosch.detekt.api/RuleSet/id/#/PointingToDeclaration/): [RuleSetId](../index.md#io.gitlab.arturbosch.detekt.api/RuleSetId///PointingToDeclaration/)   <br>
| [pathFilters](index.md#io.gitlab.arturbosch.detekt.api/RuleSet/pathFilters/#/PointingToDeclaration/)|  [jvm] <br><br>Is used to determine if a given KtFile should be analyzed at all.<br><br>~~var~~ [~~pathFilters~~](index.md#io.gitlab.arturbosch.detekt.api/RuleSet/pathFilters/#/PointingToDeclaration/)~~:~~ [PathFilters](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.md)?   <br>
| [rules](index.md#io.gitlab.arturbosch.detekt.api/RuleSet/rules/#/PointingToDeclaration/)|  [jvm] val [rules](index.md#io.gitlab.arturbosch.detekt.api/RuleSet/rules/#/PointingToDeclaration/): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)>   <br>

