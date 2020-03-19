@file:Suppress("unused")

package io.gitlab.arturbosch.detekt.api

/**
 * Wraps two different configuration which should be considered when retrieving properties.
 */
@Deprecated("Rule authors should not rely on a specific configuration kind.")
typealias CompositeConfig = io.gitlab.arturbosch.detekt.api.internal.CompositeConfig
