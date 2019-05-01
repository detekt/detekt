---
title: AnnotationExcluder - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [AnnotationExcluder](./index.html)

# AnnotationExcluder

`class AnnotationExcluder`

Primary use case for an AnnotationExcluder is to decide if a KtElement should be
excluded from further analysis. This is done by checking if a special annotation
is present over the element.

**Author**
Niklas Baudy

**Author**
Artur Bosch

**Author**
schalkms

### Constructors

| [&lt;init&gt;](-init-.html) | `AnnotationExcluder(root: KtFile, excludes: `[`SplitPattern`](../-split-pattern/index.html)`)`<br>Primary use case for an AnnotationExcluder is to decide if a KtElement should be excluded from further analysis. This is done by checking if a special annotation is present over the element. |

### Functions

| [shouldExclude](should-exclude.html) | `fun shouldExclude(annotations: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtAnnotationEntry>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is true if any given annotation name is declared in the SplitPattern which basically describes entries to exclude. |

