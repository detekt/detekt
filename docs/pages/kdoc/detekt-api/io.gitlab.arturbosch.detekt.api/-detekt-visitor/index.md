---
title: DetektVisitor - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [DetektVisitor](./index.html)

# DetektVisitor

`open class DetektVisitor : KtTreeVisitorVoid`

Basic visitor which is used inside detekt.
Guarantees a better looking name as the extended base class :).

### Constructors

| [&lt;init&gt;](-init-.html) | Basic visitor which is used inside detekt. Guarantees a better looking name as the extended base class :).`DetektVisitor()` |

### Inheritors

| [BaseRule](../-base-rule/index.html) | Defines the visiting mechanism for KtFile's.`abstract class BaseRule : `[`DetektVisitor`](./index.html)`, `[`Context`](../-context/index.html) |
| [McCabeVisitor](../../io.gitlab.arturbosch.detekt.api.internal/-mc-cabe-visitor/index.html) | Counts the cyclomatic complexity of functions.`class McCabeVisitor : `[`DetektVisitor`](./index.html) |

