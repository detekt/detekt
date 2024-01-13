package io.gitlab.arturbosch.detekt.core.util

import io.gitlab.arturbosch.detekt.api.Config

internal fun Config.isActiveOrDefault(default: Boolean): Boolean = valueOrDefault(Config.ACTIVE_KEY, default)
