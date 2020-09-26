---
title: Companion -
---
//[detekt-api](../../../index.md)/[io.gitlab.arturbosch.detekt.api](../../index.md)/[Entity](../index.md)/[Companion](index.md)



# Companion  
 [jvm] object [Companion](index.md)   


## Functions  
  
|  Name|  Summary| 
|---|---|
| [atName](at-name.md)| [jvm]  <br>Brief description  <br><br><br>Create an entity at the location of the identifier of given named declaration.<br><br>  <br>Content  <br>fun [atName](at-name.md)(element: KtNamedDeclaration): [Entity](../index.md)  <br><br><br>
| [atPackageOrFirstDecl](at-package-or-first-decl.md)| [jvm]  <br>Brief description  <br><br><br>Create an entity at the location of the package, first import or first declaration.<br><br>  <br>Content  <br>fun [atPackageOrFirstDecl](at-package-or-first-decl.md)(file: KtFile): [Entity](../index.md)  <br><br><br>
| [equals](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)| [jvm]  <br>Content  <br>open operator override fun [equals](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/equals/#kotlin.Any?/PointingToDeclaration/)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)  <br><br><br>
| [from](from.md)| [jvm]  <br>Brief description  <br><br><br>Use this factory method if the location can be calculated much more precisely than using the given PsiElement.<br><br>  <br>Content  <br>fun [from](from.md)(element: PsiElement, location: [Location](../../-location/index.md)): [Entity](../index.md)  <br><br><br>[jvm]  <br>Brief description  <br><br><br>Factory function which retrieves all needed information from the PsiElement itself.<br><br>  <br>Content  <br>fun [from](from.md)(element: PsiElement, offset: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Entity](../index.md)  <br><br><br>
| [hashCode](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [hashCode](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/hashCode/#/PointingToDeclaration/)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)  <br><br><br>
| [toString](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)| [jvm]  <br>Content  <br>open override fun [toString](../../../io.gitlab.arturbosch.detekt.api.internal/-yaml-config/-companion/index.md#kotlin/Any/toString/#/PointingToDeclaration/)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)  <br><br><br>

