---
title: Companion -
---
//[detekt-api](../../../index.md)/[io.gitlab.arturbosch.detekt.api](../../index.md)/[Location](../index.md)/[Companion](index.md)



# Companion  
 [jvm] object [Companion](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)| [jvm]  <br>Content  <br>open operator override fun [equals](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [from](from.md)| [jvm]  <br>Brief description  <br><br><br>Creates a [Location](../index.md) from a PsiElement. If the element can't be determined, the KtFile with a character offset can be used.<br><br>  <br>Content  <br>fun [from](from.md)(element: PsiElement, offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Location](../index.md)  <br><br><br>
| [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)| [jvm]  <br>Content  <br>open override fun [hashCode](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/hash-code.html)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [startLineAndColumn](start-line-and-column.md)| [jvm]  <br>Brief description  <br><br><br>Determines the line and column of a PsiElement in the source file.<br><br>  <br>Content  <br>fun [startLineAndColumn](start-line-and-column.md)(element: PsiElement, offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): PsiDiagnosticUtils.LineAndColumn  <br><br><br>
| [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)| [jvm]  <br>Content  <br>open override fun [toString](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/to-string.html)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

