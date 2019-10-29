---
title: HierarchicalConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [HierarchicalConfig](./index.html)

# HierarchicalConfig

`interface HierarchicalConfig : `[`Config`](../-config/index.html)

A configuration which keeps track of the config it got sub-config'ed from by the [subConfig](../-config/sub-config.html) function.
It's main usage is to recreate the property-path which was taken when using the [subConfig](../-config/sub-config.html) function repeatedly.

### Types

| [Parent](-parent/index.html) | Keeps track of which key was taken to [subConfig](../-config/sub-config.html) this configuration.`data class Parent` |

### Properties

| [parent](parent.html) | Returns the parent config which encloses this config part.`abstract val parent: Parent?` |

### Inheritors

| [BaseConfig](../-base-config/index.html) | Convenient base configuration which parses/casts the configuration value based on the type of the default value.`abstract class BaseConfig : `[`HierarchicalConfig`](./index.html) |

