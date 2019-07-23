---
title: BaseRule.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [BaseRule](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`BaseRule(context: `[`Context`](../-context/index.html)` = DefaultContext())`

Defines the visiting mechanism for KtFile's.

Custom rule implementations should actually use [Rule](../-rule/index.html) as base class.

The extraction of this class from [Rule](../-rule/index.html) actually resulted from the need
of running many different checks on the same KtFile but within a single
potential costly visiting process, see [MultiRule](../-multi-rule/index.html).

This base rule class abstracts over single and multi rules and allows the
detekt core engine to only care about a single type.

