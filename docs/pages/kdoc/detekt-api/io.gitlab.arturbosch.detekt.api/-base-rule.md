---
title: BaseRule - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api](index.html) / [BaseRule](./-base-rule.html)

# BaseRule

`typealias ~~BaseRule~~ = `[`BaseRule`](../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)
**Deprecated:** \nDo not use this class directly. Use Rule or MultiRule instead.\nThis class was introduced to support a common handling of the mentioned rule types.\nThis class will be made sealed in a different release and you won't be able to derive from it. \n

Defines the visiting mechanism for KtFile's.

Custom rule implementations should actually use [Rule](-rule/index.html) as base class.

The extraction of this class from [Rule](-rule/index.html) actually resulted from the need
of running many different checks on the same KtFile but within a single
potential costly visiting process, see [MultiRule](-multi-rule/index.html).

This base rule class abstracts over single and multi rules and allows the
detekt core engine to only care about a single type.

