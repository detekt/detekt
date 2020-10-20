---
title: DefaultContext -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[DefaultContext](index.md)



# DefaultContext  
 [jvm] open class [DefaultContext](index.md) : [Context](../-context/index.md)

Default [Context](../-context/index.md) implementation.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/DefaultContext/#/PointingToDeclaration/"></a>[DefaultContext](-default-context.md)| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/DefaultContext/#/PointingToDeclaration/"></a> [jvm] fun [DefaultContext](-default-context.md)()   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/clearFindings/#/PointingToDeclaration/"></a>[clearFindings](clear-findings.md)| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/clearFindings/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>override fun [clearFindings](clear-findings.md)()  <br>More info  <br>Clears previous findings.  <br><br><br>
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/report/#io.gitlab.arturbosch.detekt.api.Finding#kotlin.collections.Set[kotlin.String]#kotlin.String?/PointingToDeclaration/"></a>[report](report.md)| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/report/#io.gitlab.arturbosch.detekt.api.Finding#kotlin.collections.Set[kotlin.String]#kotlin.String?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open override fun [report](report.md)(finding: [Finding](../-finding/index.md), aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)?)  <br>More info  <br>Reports a single code smell finding.  <br><br><br>[jvm]  <br>Content  <br>open override fun [report](report.md)(findings: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>, aliases: [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>, ruleSetId: [RuleSetId](../index.md#%5Bio.gitlab.arturbosch.detekt.api%2FRuleSetId%2F%2F%2FPointingToDeclaration%2F%5D%2FClasslikes%2F-931080397)?)  <br>More info  <br>Reports a list of code smell findings.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>


## Properties  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/findings/#/PointingToDeclaration/"></a>[findings](findings.md)| <a name="io.gitlab.arturbosch.detekt.api/DefaultContext/findings/#/PointingToDeclaration/"></a> [jvm] open override val [findings](findings.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[Finding](../-finding/index.md)>Returns a copy of violations for this rule.   <br>

