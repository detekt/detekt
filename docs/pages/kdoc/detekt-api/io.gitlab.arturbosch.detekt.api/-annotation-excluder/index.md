---
title: AnnotationExcluder -
---
//[detekt-api](../../index.md)/[io.gitlab.arturbosch.detekt.api](../index.md)/[AnnotationExcluder](index.md)



# AnnotationExcluder  
 [jvm] class [AnnotationExcluder](index.md)(**root**: KtFile, **excludes**: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>)

Primary use case for an AnnotationExcluder is to decide if a KtElement should be excluded from further analysis. This is done by checking if a special annotation is present over the element.

   


## Constructors  
  
|  Name|  Summary| 
|---|---|
| <a name="io.gitlab.arturbosch.detekt.api/AnnotationExcluder/AnnotationExcluder/#org.jetbrains.kotlin.psi.KtFile#io.gitlab.arturbosch.detekt.api.SplitPattern/PointingToDeclaration/"></a>[AnnotationExcluder](-annotation-excluder.md)| <a name="io.gitlab.arturbosch.detekt.api/AnnotationExcluder/AnnotationExcluder/#org.jetbrains.kotlin.psi.KtFile#io.gitlab.arturbosch.detekt.api.SplitPattern/PointingToDeclaration/"></a> [jvm] fun [AnnotationExcluder](-annotation-excluder.md)(root: KtFile, excludes: [SplitPattern](../-split-pattern/index.md))   <br>
| <a name="io.gitlab.arturbosch.detekt.api/AnnotationExcluder/AnnotationExcluder/#org.jetbrains.kotlin.psi.KtFile#kotlin.collections.List[kotlin.String]/PointingToDeclaration/"></a>[AnnotationExcluder](-annotation-excluder.md)| <a name="io.gitlab.arturbosch.detekt.api/AnnotationExcluder/AnnotationExcluder/#org.jetbrains.kotlin.psi.KtFile#kotlin.collections.List[kotlin.String]/PointingToDeclaration/"></a> [jvm] fun [AnnotationExcluder](-annotation-excluder.md)(root: KtFile, excludes: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)>)   <br>


## Functions  
  
|  Name|  Summary| 
|---|---|
| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open operator fun [equals](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2Fequals%2F%23kotlin.Any%3F%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/hashCode/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [hashCode](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FhashCode%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| <a name="io.gitlab.arturbosch.detekt.api/AnnotationExcluder/shouldExclude/#kotlin.collections.List[org.jetbrains.kotlin.psi.KtAnnotationEntry]/PointingToDeclaration/"></a>[shouldExclude](should-exclude.md)| <a name="io.gitlab.arturbosch.detekt.api/AnnotationExcluder/shouldExclude/#kotlin.collections.List[org.jetbrains.kotlin.psi.KtAnnotationEntry]/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>fun [shouldExclude](should-exclude.md)(annotations: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)<KtAnnotationEntry>): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br>More info  <br>Is true if any given annotation name is declared in the SplitPattern which basically describes entries to exclude.  <br><br><br>
| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)| <a name="kotlin/Any/toString/#/PointingToDeclaration/"></a>[jvm]  <br>Content  <br>open fun [toString](../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#%5Bkotlin%2FAny%2FtoString%2F%23%2FPointingToDeclaration%2F%5D%2FFunctions%2F-931080397)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

