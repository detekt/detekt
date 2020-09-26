---
title: Config -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Config](index.md)



# Config  
 [jvm] 

A configuration holds information about how to configure specific rules.

interface [Config](index.md)   


## Types  
  
|  Name|  Summary| 
|---|---|
| [Companion](-companion/index.md)| [jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>
| [InvalidConfigurationError](-invalid-configuration-error/index.md)| [jvm]  <br>Brief description  <br><br><br>Is thrown when loading a configuration results in errors.<br><br>  <br>Content  <br>class [InvalidConfigurationError](-invalid-configuration-error/index.md)(**throwable**: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?) : [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html)  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [subConfig](sub-config.md)| [jvm]  <br>Brief description  <br><br><br>Tries to retrieve part of the configuration based on given key.<br><br>  <br>Content  <br>abstract fun [subConfig](sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](index.md)  <br><br><br>
| [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [valueOrDefault](value-or-default.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.<br><br>  <br>Content  <br>open fun <[T](value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](value-or-default.md)): [T](value-or-default.md)  <br><br><br>
| [valueOrNull](value-or-null.md)| [jvm]  <br>Brief description  <br><br><br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.<br><br>  <br>Content  <br>abstract fun <[T](value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](value-or-null.md)?  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [parentPath](index.md#io.gitlab.arturbosch.detekt.api/Config/parentPath/#/PointingToDeclaration/)|  [jvm] <br><br><br><br>Keeps track of which key was taken to [subConfig](sub-config.md) this configuration. Sub-sequential calls to [subConfig](sub-config.md) are tracked with '>' as a separator.<br><br><br><br>May be null if this is the top most configuration object.<br><br><br><br>open val [parentPath](index.md#io.gitlab.arturbosch.detekt.api/Config/parentPath/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>


## Inheritors  
  
|  Name| 
|---|
| [ConfigAware](../-config-aware/index.md)
| [BaseConfig](../../io.gitlab.arturbosch.detekt.api.internal/-base-config/index.md)
| [CompositeConfig](../../io.gitlab.arturbosch.detekt.api.internal/-composite-config/index.md)
| [DisabledAutoCorrectConfig](../../io.gitlab.arturbosch.detekt.api.internal/-disabled-auto-correct-config/index.md)
| [FailFastConfig](../../io.gitlab.arturbosch.detekt.api.internal/-fail-fast-config/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| [createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.md)| [jvm]  <br>Content  <br>fun [Config](index.md).[createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.md)(): [PathFilters](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.md)?  <br><br><br>
| [valueOrDefaultCommaSeparated](../../io.gitlab.arturbosch.detekt.api.internal/value-or-default-comma-separated.md)| [jvm]  <br>Content  <br>fun [Config](index.md).[valueOrDefaultCommaSeparated](../../io.gitlab.arturbosch.detekt.api.internal/value-or-default-comma-separated.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  <br><br><br>

