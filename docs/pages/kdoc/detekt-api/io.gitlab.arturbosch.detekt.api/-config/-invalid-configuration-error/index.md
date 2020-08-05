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
| [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/add-suppressed.html)| [jvm]  <br>Content  <br>override fun [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/add-suppressed.html)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html))  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [fillInStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/fill-in-stack-trace.html)| [jvm]  <br>Content  <br>open override fun [fillInStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/fill-in-stack-trace.html)(): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)  <br><br><br>
| [getLocalizedMessage](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/get-localized-message.html)| [jvm]  <br>Content  <br>open override fun [getLocalizedMessage](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/get-localized-message.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>
| [getStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/get-stack-trace.html)| [jvm]  <br>Content  <br>open override fun [getStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/get-stack-trace.html)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)>  <br><br><br>
| [getSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/get-suppressed.html)| [jvm]  <br>Content  <br>override fun [getSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/get-suppressed.html)(): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)>  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [initCause](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/init-cause.html)| [jvm]  <br>Content  <br>open override fun [initCause](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/init-cause.html)(p0: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)  <br><br><br>
| [printStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/print-stack-trace.html)| [jvm]  <br>Content  <br>open override fun [printStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/print-stack-trace.html)()  <br>open override fun [printStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/print-stack-trace.html)(p0: [PrintStream](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html))  <br>open override fun [printStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/print-stack-trace.html)(p0: [PrintWriter](https://docs.oracle.com/javase/8/docs/api/java/io/PrintWriter.html))  <br><br><br>
| [setStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/set-stack-trace.html)| [jvm]  <br>Content  <br>open override fun [setStackTrace](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/set-stack-trace.html)(p0: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)<[StackTraceElement](https://docs.oracle.com/javase/8/docs/api/java/lang/StackTraceElement.html)>)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [cause](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/cause/#/PointingToDeclaration/)|  [jvm] open override val [cause](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/cause/#/PointingToDeclaration/): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)?   <br>
| [message](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/message/#/PointingToDeclaration/)|  [jvm] open override val [message](index.md#io.gitlab.arturbosch.detekt.api/Config.InvalidConfigurationError/message/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?   <br>

