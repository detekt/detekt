---
title: Entity - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Entity](./index.html)

# Entity

`data class Entity : `[`Compactable`](../-compactable/index.html)

Stores information about a specific code fragment.

### Constructors

| [&lt;init&gt;](-init-.html) | `Entity(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, className: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, location: `[`Location`](../-location/index.html)`, ktElement: KtElement? = null)`<br>Stores information about a specific code fragment.`Entity(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, location: `[`Location`](../-location/index.html)`, ktElement: KtElement? = null)` |

### Properties

| [ktElement](kt-element.html) | `val ktElement: KtElement?` |
| [location](location.html) | `val location: `[`Location`](../-location/index.html) |
| [signature](signature.html) | `val signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| [compact](compact.html) | Contract to format implementing object to a string representation.`fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| [atName](at-name.html) | Create an entity at the location of the identifier of given named declaration.`fun atName(element: KtNamedDeclaration): `[`Entity`](./index.html) |
| [atPackageOrFirstDecl](at-package-or-first-decl.html) | Create an entity at the location of the package, first import or first declaration.`fun atPackageOrFirstDecl(file: KtFile): `[`Entity`](./index.html) |
| [from](from.html) | Factory function which retrieves all needed information from the PsiElement itself.`fun from(element: PsiElement, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Entity`](./index.html)<br>Use this factory method if the location can be calculated much more precisely than using the given PsiElement.`fun from(element: PsiElement, location: `[`Location`](../-location/index.html)`): `[`Entity`](./index.html) |

