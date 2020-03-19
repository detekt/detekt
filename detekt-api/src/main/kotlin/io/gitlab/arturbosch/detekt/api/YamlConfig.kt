@file:Suppress("unused")

package io.gitlab.arturbosch.detekt.api

/**
 * Config implementation using the yaml format. SubConfigurations can return sub maps according to the
 * yaml specification.
 */
@Deprecated("Rule authors should not rely on a specific configuration kind.")
typealias YamlConfig = io.gitlab.arturbosch.detekt.api.internal.YamlConfig
