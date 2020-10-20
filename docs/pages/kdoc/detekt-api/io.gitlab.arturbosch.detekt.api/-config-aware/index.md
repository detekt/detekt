---
title: ConfigAware -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ConfigAware](index.md)



# ConfigAware  
 [jvm] interface [ConfigAware](index.md) : [Config](../-config/index.md)

Interface which is implemented by each Rule class to provide utility functions to retrieve specific or generic properties from the underlying detekt configuration file.



Be aware that there are three config levels by default:

<ul><li>the top level config layer specifies rule sets and detekt engine properties</li><li>the rule set level specifies properties concerning the whole rule set and rules</li><li>the rule level provides additional properties which are used to configure rules</li></ul>

This interface operates on the rule set level as the rule set config is passed to each rule in the #RuleSetProvider interface. This is due the fact that users create the rule set and all rules upfront and letting them 'sub config' the rule set config would be error-prone.

   


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/subConfig/#kotlin.String/PointingToDeclaration/"></a>[subConfig](sub-config.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/subConfig/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [subConfig](sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](../-config/index.md)  <br>More info  <br>Tries to retrieve part of the configuration based on given key.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[valueOrDefault](value-or-default.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun <[T](value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](value-or-default.md)): [T](value-or-default.md)  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[valueOrNull](value-or-null.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun <[T](value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](value-or-null.md)?  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/withAutoCorrect/#kotlin.Function0[kotlin.Unit]/PointingToDeclaration/"></a>[withAutoCorrect](with-auto-correct.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/withAutoCorrect/#kotlin.Function0[kotlin.Unit]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [withAutoCorrect](with-auto-correct.md)(block: () -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  <br>More info  <br>If your rule supports to automatically correct the misbehaviour of underlying smell, specify your code inside this method call, to allow the user of your rule to trigger auto correction only when needed.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/active/#/PointingToDeclaration/"></a>[active](active.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/active/#/PointingToDeclaration/"></a> [jvm] open val [active](active.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)Is this rule specified as active in configuration?   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/autoCorrect/#/PointingToDeclaration/"></a>[autoCorrect](auto-correct.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/autoCorrect/#/PointingToDeclaration/"></a> [jvm] open val [autoCorrect](auto-correct.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)Does this rule have auto correct specified in configuration?   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/parentPath/#/PointingToDeclaration/"></a>[parentPath](parent-path.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/parentPath/#/PointingToDeclaration/"></a> [jvm] open val [parentPath](parent-path.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?Keeps track of which key was taken to [subConfig](../-config/sub-config.md) this configuration.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/ruleId/#/PointingToDeclaration/"></a>[ruleId](rule-id.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/ruleId/#/PointingToDeclaration/"></a> [jvm] abstract val [ruleId](rule-id.md): [RuleId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)Id which is used to retrieve the sub config for the rule implementing this interface.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/ruleSetConfig/#/PointingToDeclaration/"></a>[ruleSetConfig](rule-set-config.md)| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware/ruleSetConfig/#/PointingToDeclaration/"></a> [jvm] abstract val [ruleSetConfig](rule-set-config.md): [Config](../-config/index.md)Wrapped configuration of the ruleSet this rule is in.   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api/Rule///PointingToDeclaration/"></a>[Rule](../-rule/index.md)

