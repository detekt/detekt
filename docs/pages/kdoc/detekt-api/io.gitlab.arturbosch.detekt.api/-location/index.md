---
title: Location - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Location](./index.html)

# Location

`data class Location : `[`Compactable`](../-compactable/index.html)

Specifies a position within a source code fragment.

### Constructors

| [&lt;init&gt;](-init-.html) | `Location(source: `[`SourceLocation`](../-source-location/index.html)`, text: `[`TextLocation`](../-text-location/index.html)`, locationString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, file: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>Specifies a position within a source code fragment. |

### Properties

| [file](file.html) | `val file: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [locationString](location-string.html) | `val locationString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [source](source.html) | `val source: `[`SourceLocation`](../-source-location/index.html) |
| [text](text.html) | `val text: `[`TextLocation`](../-text-location/index.html) |

### Functions

| [compact](compact.html) | `fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Contract to format implementing object to a string representation. |

### Inherited Functions

| [compactWithSignature](../-compactable/compact-with-signature.html) | `open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Same as [compact](../-compactable/compact.html) except the content should contain a substring which represents this exact findings via a custom identifier. |

### Companion Object Functions

| [from](from.html) | `fun from(element: PsiElement, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Location`](./index.html)<br>Creates a [Location](./index.html) from a [PsiElement](#). If the element can't be determined, the [KtFile](#) with a character offset can be used. |
| [startLineAndColumn](start-line-and-column.html) | `fun startLineAndColumn(element: PsiElement, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): LineAndColumn`<br>Determines the line and column of a [PsiElement](#) in the source file. |

