package io.gitlab.arturbosch.detekt.api.internal

/**
 * Annotated [io.gitlab.arturbosch.detekt.api.Rule] or [io.gitlab.arturbosch.detekt.api.RuleSetProvider]
 * is active by default.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActiveByDefault(
    /**
     * The Detekt version since when the rule is active by default. This can be left blank
     * if activation version is not known.
     */
    val since: String = ""
)
