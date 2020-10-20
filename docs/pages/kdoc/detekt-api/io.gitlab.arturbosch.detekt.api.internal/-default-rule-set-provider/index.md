---
title: DefaultRuleSetProvider -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[DefaultRuleSetProvider](index.md)



# DefaultRuleSetProvider  
 [jvm] interface [DefaultRuleSetProvider](index.md) : [RuleSetProvider](../../io.gitlab.arturbosch.detekt.api/-rule-set-provider/index.md)

Interface which marks sub-classes as provided by detekt via the rules sub-module.



Allows to implement "--disable-default-rulesets" effectively without the need to manage a list of rule set names.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/RuleSetProvider/instance/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[instance](../../io.gitlab.arturbosch.detekt.api/-rule-set-provider/instance.md)| <a name="io.gitlab.arturbosch.detekt.api/RuleSetProvider/instance/#io.gitlab.arturbosch.detekt.api.Config/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [instance](../../io.gitlab.arturbosch.detekt.api/-rule-set-provider/instance.md)(config: [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.md)): [RuleSet](../../io.gitlab.arturbosch.detekt.api/-rule-set/index.md)  <br>More info  <br>This function must be implemented to provide custom rule sets.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal/DefaultRuleSetProvider/ruleSetId/#/PointingToDeclaration/"></a>[ruleSetId](rule-set-id.md)| <a name="io.gitlab.arturbosch.detekt.api.internal/DefaultRuleSetProvider/ruleSetId/#/PointingToDeclaration/"></a> [jvm] abstract val [ruleSetId](rule-set-id.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)Every rule set must be pre-configured with an ID to validate if this rule set must be created for current analysis.   <br>

