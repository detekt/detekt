---
title: LazyRegex - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [LazyRegex](./index.html)

# LazyRegex

`class LazyRegex : `[`ReadOnlyProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.properties/-read-only-property/index.html)`<`[`Rule`](../-rule/index.html)`, `[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`>`

LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules.
It computes the value once when reaching the point of its usage and returns the same
value when requested again.

`key` &amp; `default` are used to retrieve a value from config.

### Constructors

| [&lt;init&gt;](-init-.html) | LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules. It computes the value once when reaching the point of its usage and returns the same value when requested again.`LazyRegex(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| [getValue](get-value.html) | `fun getValue(thisRef: `[`Rule`](../-rule/index.html)`, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>): `[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html) |

