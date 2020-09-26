---
title: InvalidConfigurationError -
---
//[detekt-api](../../../index.md)/[io.gitlab.arturbosch.detekt.api](../../index.md)/[Config](../index.md)/[InvalidConfigurationError](index.md)



# InvalidConfigurationError  
 [jvm] 

Is thrown when loading a configuration results in errors.

class [InvalidConfigurationError](index.md)(**throwable**: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?) : [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [InvalidConfigurationError](-invalid-configuration-error.md)|  [jvm] fun [InvalidConfigurationError](-invalid-configuration-error.md)(throwable: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [addSuppressed](index.md#kotlin/Throwable/addSuppressed/#kotlin.Throwable/PointingToDeclaration/)| [jvm]  <br>Content  <br>override fun [addSuppressed](index.md#kotlin/Throwable/addSuppressed/#kotlin.Throwable/PointingToDeclaration/)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html))  <br><br><br>
| [equals](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [fillInStackTrace](index.md#kotlin/Throwable/fillInStackTrace/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [fillInStackTrace](index.md#kotlin/Throwable/fillInStackTrace/#/PointingToDeclaration/)(): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)  <br><br><br>
| [getLocalizedMessage](index.md#kotlin/Throwable/getLocalizedMessage/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [getLocalizedMessage](index.md#kotlin/Throwable/getLocalizedMessage/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [getStackTrace](index.md#kotlin/Throwable/getStackTrace/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [getStackTrace](index.md#kotlin/Throwable/getStackTrace/#/PointingToDeclaration/)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)>  <br><br><br>
| [getSuppressed](index.md#kotlin/Throwable/getSuppressed/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>override fun [getSuppressed](index.md#kotlin/Throwable/getSuppressed/#/PointingToDeclaration/)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)>  <br><br><br>
| [hashCode](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [initCause](index.md#kotlin/Throwable/initCause/#kotlin.Throwable/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [initCause](index.md#kotlin/Throwable/initCause/#kotlin.Throwable/PointingToDeclaration/)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)  <br><br><br>
| [printStackTrace](index.md#kotlin/Throwable/printStackTrace/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [printStackTrace](index.md#kotlin/Throwable/printStackTrace/#/PointingToDeclaration/)()  <br>open override fun [printStackTrace](index.md#kotlin/Throwable/printStackTrace/#java.io.PrintStream/PointingToDeclaration/)(p0: [PrintStream](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html))  <br>open override fun [printStackTrace](index.md#kotlin/Throwable/printStackTrace/#java.io.PrintWriter/PointingToDeclaration/)(p0: [PrintWriter](https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html))  <br><br><br>
| [setStackTrace](index.md#kotlin/Throwable/setStackTrace/#kotlin.Array[java.lang.StackTraceElement]/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [setStackTrace](index.md#kotlin/Throwable/setStackTrace/#kotlin.Array[java.lang.StackTraceElement]/PointingToDeclaration/)(p0: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)>)  <br><br><br>
| [toString](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [cause](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/cause/#/PointingToDeclaration/)|  [jvm] open override val [cause](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/cause/#/PointingToDeclaration/): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/message/#/PointingToDeclaration/)|  [jvm] open override val [message](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>

