---
title: Severity -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Severity](index.md)



# Severity  
 [jvm] 

Rules can classified into different severity grades. Maintainer can choose a grade which is most harmful to their projects.

enum [Severity](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)<[Severity](index.md)>    


## Entries  
  
|  Name|  Summary| 
|---|---|
| [CodeSmell](-code-smell/index.md)|  [jvm] <br><br>Represents clean coding violations which may lead to maintainability issues.<br><br>[CodeSmell](-code-smell/index.md)()  <br>  <br>   <br>
| [Style](-style/index.md)|  [jvm] <br><br>Inspections in this category detect violations of code syntax styles.<br><br>[Style](-style/index.md)()  <br>  <br>   <br>
| [Warning](-warning/index.md)|  [jvm] <br><br>Corresponds to issues that do not prevent the code from working, but may nevertheless represent coding inefficiencies.<br><br>[Warning](-warning/index.md)()  <br>  <br>   <br>
| [Defect](-defect/index.md)|  [jvm] <br><br>Corresponds to coding mistakes which could lead to unwanted behavior.<br><br>[Defect](-defect/index.md)()  <br>  <br>   <br>
| [Minor](-minor/index.md)|  [jvm] <br><br>Represents code quality issues which only slightly impact the code quality.<br><br>[Minor](-minor/index.md)()  <br>  <br>   <br>
| [Maintainability](-maintainability/index.md)|  [jvm] <br><br>Issues in this category make the source code confusing and difficult to maintain.<br><br>[Maintainability](-maintainability/index.md)()  <br>  <br>   <br>
| [Security](-security/index.md)|  [jvm] <br><br>Places in the source code that can be exploited and possibly result in significant damage.<br><br>[Security](-security/index.md)()  <br>  <br>   <br>
| [Performance](-performance/index.md)|  [jvm] <br><br>Places in the source code which degrade the performance of the application.<br><br>[Performance](-performance/index.md)()  <br>  <br>   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| [compareTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/compare-to.html)| [jvm]  <br>Content  <br>operator override fun [compareTo](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/compare-to.html)(other: [Severity](index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [describeConstable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/describe-constable.html)| [jvm]  <br>Content  <br>~~override~~ ~~fun~~ [~~describeConstable~~](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/describe-constable.html)~~(~~~~)~~~~:~~ [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)<[Enum.EnumDesc](https://docs.oracle.com/javase/8/docs/api/java/lang/Enum.EnumDesc.html)<[Severity](index.md)>>  <br><br><br>
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/equals.html)| [jvm]  <br>Content  <br>operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [finalize](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/finalize.html)| [jvm]  <br>Content  <br>override fun [finalize](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/finalize.html)()  <br><br><br>
| [getDeclaringClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/get-declaring-class.html)| [jvm]  <br>Content  <br>override fun [getDeclaringClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/get-declaring-class.html)(): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[Severity](index.md)>  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/hash-code.html)| [jvm]  <br>Content  <br>override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| [name](index.md#io.gitlab.arturbosch.detekt.api/Severity/name/#/PointingToDeclaration/)|  [jvm] override val [name](index.md#io.gitlab.arturbosch.detekt.api/Severity/name/#/PointingToDeclaration/): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| [ordinal](index.md#io.gitlab.arturbosch.detekt.api/Severity/ordinal/#/PointingToDeclaration/)|  [jvm] override val [ordinal](index.md#io.gitlab.arturbosch.detekt.api/Severity/ordinal/#/PointingToDeclaration/): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

