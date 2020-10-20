---
title: from -
---
//[detekt-api](../../../index.md)/[io.gitlab.arturbosch.detekt.api](../../index.md)/[Entity](../index.md)/[Companion](index.md)/[from](from.md)



# from  
[jvm]  
Content  
fun [from](from.md)(element: PsiElement, offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0): [Entity](../index.md)  
More info  


Factory function which retrieves all needed information from the PsiElement itself.

  


[jvm]  
Content  
fun [from](from.md)(element: PsiElement, location: [Location](../../-location/index.md)): [Entity](../index.md)  
More info  


Use this factory method if the location can be calculated much more precisely than using the given PsiElement.

  



