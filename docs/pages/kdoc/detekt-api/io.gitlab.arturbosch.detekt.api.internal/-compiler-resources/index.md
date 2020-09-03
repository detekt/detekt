---
title: CompilerResources -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api.internal](../index.md)/[CompilerResources](index.md)



# CompilerResources  
 [jvm] 

Provides compiler resources.

data class [CompilerResources](index.md)(**languageVersionSettings**: LanguageVersionSettings, **dataFlowValueFactory**: DataFlowValueFactory)   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| [CompilerResources](-compiler-resources.md)|  [jvm] fun [CompilerResources](-compiler-resources.md)(languageVersionSettings: LanguageVersionSettings, dataFlowValueFactory: DataFlowValueFactory)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [component1](component1.md)| [jvm]  <br>Content  <br>operator fun [component1](component1.md)(): LanguageVersionSettings  <br><br><br>
| [component2](component2.md)| [jvm]  <br>Content  <br>operator fun [component2](component2.md)(): DataFlowValueFactory  <br><br><br>
| [copy](copy.md)| [jvm]  <br>Content  <br>fun [copy](copy.md)(languageVersionSettings: LanguageVersionSettings, dataFlowValueFactory: DataFlowValueFactory): [CompilerResources](index.md)  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [dataFlowValueFactory](index.md#io.gitlab.arturbosch.detekt.api.internal/CompilerResources/dataFlowValueFactory/#/PointingToDeclaration/)|  [jvm] val [dataFlowValueFactory](index.md#io.gitlab.arturbosch.detekt.api.internal/CompilerResources/dataFlowValueFactory/#/PointingToDeclaration/): DataFlowValueFactory   <br>
| [languageVersionSettings](index.md#io.gitlab.arturbosch.detekt.api.internal/CompilerResources/languageVersionSettings/#/PointingToDeclaration/)|  [jvm] val [languageVersionSettings](index.md#io.gitlab.arturbosch.detekt.api.internal/CompilerResources/languageVersionSettings/#/PointingToDeclaration/): LanguageVersionSettings   <br>

