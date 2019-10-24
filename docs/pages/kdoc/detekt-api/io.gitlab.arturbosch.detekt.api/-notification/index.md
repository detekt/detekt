---
title: Notification - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Notification](./index.html)

# Notification

`interface Notification`

Any kind of notification which should be printed to the console.
For example when using the formatting rule set, any change to
your kotlin file is a notification.

### Properties

| [message](message.html) | `abstract val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| [SimpleNotification](../../io.gitlab.arturbosch.detekt.api.internal/-simple-notification/index.html) | `data class SimpleNotification : `[`Notification`](./index.html) |

