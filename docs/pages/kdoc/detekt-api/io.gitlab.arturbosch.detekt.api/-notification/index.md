---
title: Notification - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Notification](./index.html)

# Notification

`interface Notification`

Any kind of notification which should be printed to the console.
For example when using the formatting rule set, any change to
your kotlin file is a notification.

### Types

| [Level](-level/index.html) | Level of severity of the notification`enum class Level` |

### Properties

| [isError](is-error.html) | `open val isError: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [level](level.html) | `abstract val level: Level` |
| [message](message.html) | `abstract val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| [SimpleNotification](../../io.gitlab.arturbosch.detekt.api.internal/-simple-notification/index.html) | `data class SimpleNotification : `[`Notification`](./index.html) |

