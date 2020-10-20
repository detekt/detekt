---
title: Config -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Config](index.md)



# Config  
 [jvm] interface [Config](index.md)

A configuration holds information about how to configure specific rules.

   


## Types  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Config.Companion///PointingToDeclaration/"></a>[Companion](-companion/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Config.Companion///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>object [Companion](-companion/index.md)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError///PointingToDeclaration/"></a>[InvalidConfigurationError](-invalid-configuration-error/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError///PointingToDeclaration/"></a>[jvm]  <br>Content  <br>class [InvalidConfigurationError](-invalid-configuration-error/index.md)(**throwable**: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?) : [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html)  <br>More info  <br>Is thrown when loading a configuration results in errors.  <br><br><br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config/subConfig/#kotlin.String/PointingToDeclaration/"></a>[subConfig](sub-config.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/subConfig/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun [subConfig](sub-config.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [Config](index.md)  <br>More info  <br>Tries to retrieve part of the configuration based on given key.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[valueOrDefault](value-or-default.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrDefault/#kotlin.String#TypeParam(bounds=[kotlin.Any])/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun <[T](value-or-default.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrDefault](value-or-default.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [T](value-or-default.md)): [T](value-or-default.md)  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[valueOrNull](value-or-null.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/valueOrNull/#kotlin.String/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>abstract fun <[T](value-or-null.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)> [valueOrNull](value-or-null.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](value-or-null.md)?  <br>More info  <br>Retrieves a sub configuration or value based on given key.  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Config/parentPath/#/PointingToDeclaration/"></a>[parentPath](parent-path.md)| <a name="io.gitlab.arturbosch.detekt.api/Config/parentPath/#/PointingToDeclaration/"></a> [jvm] open val [parentPath](parent-path.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?Keeps track of which key was taken to [subConfig](sub-config.md) this configuration.   <br>


## Inheritors  
  
|  Name| 
|---|
| <a name="io.gitlab.arturbosch.detekt.api/ConfigAware///PointingToDeclaration/"></a>[ConfigAware](../-config-aware/index.md)
| <a name="io.gitlab.arturbosch.detekt.api.internal/BaseConfig///PointingToDeclaration/"></a>[BaseConfig](../../io.gitlab.arturbosch.detekt.api.internal/-base-config/index.md)
| <a name="io.gitlab.arturbosch.detekt.api.internal/CompositeConfig///PointingToDeclaration/"></a>[CompositeConfig](../../io.gitlab.arturbosch.detekt.api.internal/-composite-config/index.md)
| <a name="io.gitlab.arturbosch.detekt.api.internal/DisabledAutoCorrectConfig///PointingToDeclaration/"></a>[DisabledAutoCorrectConfig](../../io.gitlab.arturbosch.detekt.api.internal/-disabled-auto-correct-config/index.md)
| <a name="io.gitlab.arturbosch.detekt.api.internal/FailFastConfig///PointingToDeclaration/"></a>[FailFastConfig](../../io.gitlab.arturbosch.detekt.api.internal/-fail-fast-config/index.md)


## Extensions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api.internal//createPathFilters/io.gitlab.arturbosch.detekt.api.Config#/PointingToDeclaration/"></a>[createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.md)| <a name="io.gitlab.arturbosch.detekt.api.internal//createPathFilters/io.gitlab.arturbosch.detekt.api.Config#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [Config](index.md).[createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.md)(): [PathFilters](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.md)?  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api.internal//valueOrDefaultCommaSeparated/io.gitlab.arturbosch.detekt.api.Config#kotlin.String#kotlin.collections.List[kotlin.String]/PointingToDeclaration/"></a>[valueOrDefaultCommaSeparated](../../io.gitlab.arturbosch.detekt.api.internal/value-or-default-comma-separated.md)| <a name="io.gitlab.arturbosch.detekt.api.internal//valueOrDefaultCommaSeparated/io.gitlab.arturbosch.detekt.api.Config#kotlin.String#kotlin.collections.List[kotlin.String]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [Config](index.md).[valueOrDefaultCommaSeparated](../../io.gitlab.arturbosch.detekt.api.internal/value-or-default-comma-separated.md)(key: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), default: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>  <br><br><br>

