---
title: Severity -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[Severity](index.md)



# Severity  
 [jvm] enum [Severity](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)<[Severity](index.md)> 

Rules can classified into different severity grades. Maintainer can choose a grade which is most harmful to their projects.

   


## Entries  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Severity.CodeSmell///PointingToDeclaration/"></a>[CodeSmell](-code-smell/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.CodeSmell///PointingToDeclaration/"></a> [jvm] [CodeSmell](-code-smell/index.md)()  <br>Represents clean coding violations which may lead to maintainability issues.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Style///PointingToDeclaration/"></a>[Style](-style/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Style///PointingToDeclaration/"></a> [jvm] [Style](-style/index.md)()  <br>Inspections in this category detect violations of code syntax styles.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Warning///PointingToDeclaration/"></a>[Warning](-warning/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Warning///PointingToDeclaration/"></a> [jvm] [Warning](-warning/index.md)()  <br>Corresponds to issues that do not prevent the code from working, but may nevertheless represent coding inefficiencies.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Defect///PointingToDeclaration/"></a>[Defect](-defect/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Defect///PointingToDeclaration/"></a> [jvm] [Defect](-defect/index.md)()  <br>Corresponds to coding mistakes which could lead to unwanted behavior.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Minor///PointingToDeclaration/"></a>[Minor](-minor/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Minor///PointingToDeclaration/"></a> [jvm] [Minor](-minor/index.md)()  <br>Represents code quality issues which only slightly impact the code quality.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Maintainability///PointingToDeclaration/"></a>[Maintainability](-maintainability/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Maintainability///PointingToDeclaration/"></a> [jvm] [Maintainability](-maintainability/index.md)()  <br>Issues in this category make the source code confusing and difficult to maintain.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Security///PointingToDeclaration/"></a>[Security](-security/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Security///PointingToDeclaration/"></a> [jvm] [Security](-security/index.md)()  <br>Places in the source code that can be exploited and possibly result in significant damage.   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity.Performance///PointingToDeclaration/"></a>[Performance](-performance/index.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity.Performance///PointingToDeclaration/"></a> [jvm] [Performance](-performance/index.md)()  <br>Places in the source code which degrade the performance of the application.   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Enum/compareTo/#io.gitlab.arturbosch.detekt.api.Severity/PointingToDeclaration/"></a>[compareTo](-performance/index.md#%5Bkotlin%2FEnum%2FcompareTo%2F%23io.gitlab.arturbosch.detekt.api.Severity%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/compareTo/#io.gitlab.arturbosch.detekt.api.Severity/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator override fun [compareTo](-performance/index.md#%5Bkotlin%2FEnum%2FcompareTo%2F%23io.gitlab.arturbosch.detekt.api.Severity%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Severity](index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Enum/describeConstable/#/PointingToDeclaration/"></a>[describeConstable](-performance/index.md#%5Bkotlin%2FEnum%2FdescribeConstable%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/describeConstable/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>~~override~~ ~~fun~~ [~~describeConstable~~](-performance/index.md#%5Bkotlin%2FEnum%2FdescribeConstable%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)~~(~~~~)~~~~:~~ [Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)<[Enum.EnumDesc](https://docs.oracle.com/javase/8/docs/api/java/lang/Enum.EnumDesc.html)<[Severity](index.md)>>  <br><br><br>
| <a name="kotlin/Enum/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](-performance/index.md#%5Bkotlin%2FEnum%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>operator override fun [equals](-performance/index.md#%5Bkotlin%2FEnum%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Enum/finalize/#/PointingToDeclaration/"></a>[finalize](-performance/index.md#%5Bkotlin%2FEnum%2Ffinalize%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/finalize/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [finalize](-performance/index.md#%5Bkotlin%2FEnum%2Ffinalize%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)()  <br><br><br>
| <a name="kotlin/Enum/getDeclaringClass/#/PointingToDeclaration/"></a>[getDeclaringClass](-performance/index.md#%5Bkotlin%2FEnum%2FgetDeclaringClass%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/getDeclaringClass/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [getDeclaringClass](-performance/index.md#%5Bkotlin%2FEnum%2FgetDeclaringClass%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Class](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html)<[Severity](index.md)>  <br><br><br>
| <a name="kotlin/Enum/hashCode/#/PointingToDeclaration/"></a>[hashCode](-performance/index.md#%5Bkotlin%2FEnum%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>override fun [hashCode](-performance/index.md#%5Bkotlin%2FEnum%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="kotlin/Enum/toString/#/PointingToDeclaration/"></a>[toString](-performance/index.md#%5Bkotlin%2FEnum%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Enum/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [toString](-performance/index.md#%5Bkotlin%2FEnum%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/Severity/name/#/PointingToDeclaration/"></a>[name](name.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity/name/#/PointingToDeclaration/"></a> [jvm] val [name](name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)   <br>
| <a name="io.gitlab.arturbosch.detekt.api/Severity/ordinal/#/PointingToDeclaration/"></a>[ordinal](ordinal.md)| <a name="io.gitlab.arturbosch.detekt.api/Severity/ordinal/#/PointingToDeclaration/"></a> [jvm] val [ordinal](ordinal.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)   <br>

