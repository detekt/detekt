---
title: Entity - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Entity](./index.html)

# Entity

`data class Entity : `[`Compactable`](../-compactable/index.html)

Stores information about a specific code fragment.

**Author**
Artur Bosch

### Constructors

| [&lt;init&gt;](-init-.html) | `Entity(name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, className: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, location: `[`Location`](../-location/index.html)`, ktElement: KtElement? = null)`<br>Stores information about a specific code fragment. |

### Properties

| [className](class-name.html) | `val className: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ktElement](kt-element.html) | `val ktElement: KtElement?` |
| [location](location.html) | `val location: `[`Location`](../-location/index.html) |
| [name](name.html) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [signature](signature.html) | `val signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| [compact](compact.html) | `fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inherited Functions

| [compactWithSignature](../-compactable/compact-with-signature.html) | `open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Functions

| [from](from.html) | `fun from(element: PsiElement, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Entity`](./index.html)<br>Factory function which retrieves all needed information from the PsiElement itself. |

