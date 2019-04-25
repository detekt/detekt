---
title: DetektVisitor - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [DetektVisitor](./index.html)

# DetektVisitor

`open class DetektVisitor : KtTreeVisitorVoid`

Basic visitor which is used inside detekt.
Guarantees a better looking name as the extended base class :).

**Author**
Artur Bosch

### Constructors

| [&lt;init&gt;](-init-.html) | `DetektVisitor()`<br>Basic visitor which is used inside detekt. Guarantees a better looking name as the extended base class :). |

### Inheritors

| [BaseRule](../-base-rule/index.html) | `abstract class BaseRule : `[`DetektVisitor`](./index.html)`, `[`Context`](../-context/index.html)<br>Defines the visiting mechanism for KtFile's. |
| [McCabeVisitor](../../io.gitlab.arturbosch.detekt.api.internal/-mc-cabe-visitor/index.html) | `class McCabeVisitor : `[`DetektVisitor`](./index.html)<br>Counts the cyclomatic complexity of functions. |

