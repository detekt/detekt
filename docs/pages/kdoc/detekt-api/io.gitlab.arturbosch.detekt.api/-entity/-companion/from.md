---
title: from -
---
//[detekt-api](../../../index.md)/[io.gitlab.arturbosch.detekt.api](../../index.md)/[Entity](../index.md)/[Companion](index.md)/[from](from.md)



# from  
[jvm]  
Brief description  


Factory function which retrieves all needed information from the PsiElement itself.

  
Content  
fun [from](from.md)(element: PsiElement, offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Entity](../index.md)  


[jvm]  
Brief description  


Use this factory method if the location can be calculated much more precisely than using the given PsiElement.

  
Content  
fun [from](from.md)(element: PsiElement, location: [Location](../../-location/index.md)): [Entity](../index.md)  



