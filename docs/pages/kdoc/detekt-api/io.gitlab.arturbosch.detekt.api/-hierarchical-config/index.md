---
title: HierarchicalConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [HierarchicalConfig](./index.html)

# HierarchicalConfig

`interface ~~HierarchicalConfig~~ : `[`Config`](../-config/index.html)
**Deprecated:** \nA Config is a long lived object and is derived via subConfig a lot.\nKeeping track of the parent it was derived, creates long-lived object chains which takes the GC longer to release them.\nIt can even lead to OOM if detekt get's embedded in an other application which reuses the top most Config object. \nThe property 'parentPath' of the Config interface can be used as a replacement for parent.key calls.\n

A configuration which keeps track of the config it got sub-config'ed from by the [subConfig](../-config/sub-config.html) function.
It's main usage is to recreate the property-path which was taken when using the [subConfig](../-config/sub-config.html) function repeatedly.

### Types

| [Parent](-parent/index.html) | Keeps track of which key was taken to [subConfig](../-config/sub-config.html) this configuration.`data class Parent` |

### Properties

| [parent](parent.html) | Returns the parent config which encloses this config part.`abstract val parent: Parent?` |

### Extension Functions

| [createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.html) | `fun `[`Config`](../-config/index.html)`.createPathFilters(): `[`PathFilters`](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.html)`?` |

### Inheritors

| [BaseConfig](../../io.gitlab.arturbosch.detekt.api.internal/-base-config/index.html) | Convenient base configuration which parses/casts the configuration value based on the type of the default value.`abstract class BaseConfig : `[`HierarchicalConfig`](./index.html) |

