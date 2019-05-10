---
title: Config.empty - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Config](index.html) / [empty](./empty.html)

# empty

`val empty: `[`Config`](index.html)

An empty configuration with no properties.
This config should only be used in test cases.
Always returns the default value except when 'active' is queried, it returns true .

