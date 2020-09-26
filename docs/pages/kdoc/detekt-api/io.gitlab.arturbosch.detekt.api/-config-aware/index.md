---
title: ConfigAware -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[ConfigAware](index.md)



# ConfigAware  
 [jvm] 



Interface which is implemented by each Rule class to provide utility functions to retrieve specific or generic properties from the underlying detekt configuration file.



Be aware that there are three config levels by default:

<ul><li>the top level config layer specifies rule sets and detekt engine properties</li><li>the rule set level specifies properties concerning the whole rule set and rules</li><li>the rule level provides additional properties which are used to configure rules</li></ul>

This interface operates on the rule set level as the rule set config is passed to each rule in the #RuleSetProvider interface. This is due the fact that users create the rule set and all rules upfront and letting them 'sub config' the rule set config would be error-prone.



interface [ConfigAware](index.md) : [Config](../-config/index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [subConfig](sub-config.md)| [jvm]  <br>Brief description  <br><br><br>Tries to retrieve part of the configuration based on given key.<br><br>  <br>Content  <br>open override fun [subConfig](sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](../-config/index.md)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [valueOrDefault](value-or-default.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.<br><br>  <br>Content  <br>open override fun <[T](value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](value-or-default.md)): [T](value-or-default.md)  <br><br><br>
| [valueOrNull](value-or-null.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.<br><br>  <br>Content  <br>open override fun <[T](value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](value-or-null.md)?  <br><br><br>
| [withAutoCorrect](with-auto-correct.md)| [jvm]  <br>Brief description  <br><br><br>If your rule supports to automatically correct the misbehaviour of underlying smell, specify your code inside this method call, to allow the user of your rule to trigger auto correction only when needed.<br><br>  <br>Content  <br>open fun [withAutoCorrect](with-auto-correct.md)(block: () -> [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html))  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [active](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/active/#/PointingToDeclaration/)|  [jvm] <br><br>Is this rule specified as active in configuration? If an rule is not specified in the underlying configuration, we assume it should not be run.<br><br>open val [active](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/active/#/PointingToDeclaration/): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| [autoCorrect](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/autoCorrect/#/PointingToDeclaration/)|  [jvm] <br><br>Does this rule have auto correct specified in configuration? For auto correction to work the rule set itself enable it.<br><br>open val [autoCorrect](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/autoCorrect/#/PointingToDeclaration/): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)   <br>
| [parentPath](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/parentPath/#/PointingToDeclaration/)|  [jvm] <br><br><br><br>Keeps track of which key was taken to [subConfig](sub-config.md) this configuration. Sub-sequential calls to [subConfig](sub-config.md) are tracked with '>' as a separator.<br><br><br><br>May be null if this is the top most configuration object.<br><br><br><br>open override val [parentPath](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/parentPath/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>
| [ruleId](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/ruleId/#/PointingToDeclaration/)|  [jvm] <br><br>Id which is used to retrieve the sub config for the rule implementing this interface.<br><br>abstract val [ruleId](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/ruleId/#/PointingToDeclaration/): [RuleId](../index.md#io.gitlab.arturbosch.detekt.api/RuleId///PointingToDeclaration/)   <br>
| [ruleSetConfig](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/ruleSetConfig/#/PointingToDeclaration/)|  [jvm] <br><br>Wrapped configuration of the ruleSet this rule is in. Use #valueOrDefault function to retrieve properties specified for the rule implementing this interface instead. Only use this property directly if you need a specific rule set property.<br><br>abstract val [ruleSetConfig](index.md#io.gitlab.arturbosch.detekt.api/ConfigAware/ruleSetConfig/#/PointingToDeclaration/): [Config](../-config/index.md)   <br>


## Inheritors  
  
|  Name| 
|---|
| [Rule](../-rule/index.md)

