---
title: RuleSet -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[RuleSet](index.md)



# RuleSet  
 [jvm] class [RuleSet](index.md)(**id**: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397), **rules**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)>)

A rule set is a collection of rules and must be defined within a rule set provider implementation.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/RuleSet/#kotlin.String#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.internal.BaseRule]/PointingToDeclaration/"></a>[RuleSet](-rule-set.md)| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/RuleSet/#kotlin.String#kotlin.collections.List[io.gitlab.arturbosch.detekt.api.internal.BaseRule]/PointingToDeclaration/"></a> [jvm] fun [RuleSet](-rule-set.md)(id: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397), rules: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/accept/#org.jetbrains.kotlin.psi.KtFile#org.jetbrains.kotlin.resolve.BindingContext/PointingToDeclaration/"></a>[accept](accept.md)| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/accept/#org.jetbrains.kotlin.psi.KtFile#org.jetbrains.kotlin.resolve.BindingContext/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~fun~~ [~~accept~~](accept.md)~~(~~~~file~~~~:~~ KtFile~~,~~ ~~bindingContext~~~~:~~ BindingContext ~~= BindingContext.EMPTY~~~~)~~~~:~~ [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>  <br>More info  <br>Visits given file with all rules of this rule set, returning a list of all code smell findings.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/id/#/PointingToDeclaration/"></a>[id](id.md)| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/id/#/PointingToDeclaration/"></a> [jvm] val [id](id.md): [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/pathFilters/#/PointingToDeclaration/"></a>[pathFilters](path-filters.md)| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/pathFilters/#/PointingToDeclaration/"></a> [jvm] ~~var~~ [~~pathFilters~~](path-filters.md)~~:~~ [PathFilters](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.md)? ~~= null~~Is used to determine if a given KtFile should be analyzed at all.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/rules/#/PointingToDeclaration/"></a>[rules](rules.md)| <a name="io.gitlab.arturbosch.detekt.api/RuleSet/rules/#/PointingToDeclaration/"></a> [jvm] val [rules](rules.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[BaseRule](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.md)>   <br>

