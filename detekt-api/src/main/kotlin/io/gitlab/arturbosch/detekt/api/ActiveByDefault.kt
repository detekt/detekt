package io.gitlab.arturbosch.detekt.api

/**
 * Annotated [io.gitlab.arturbosch.detekt.api.Rule] or [io.gitlab.arturbosch.detekt.api.RuleSetProvider]
 * is active by default.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActiveByDefault(
    /**
     *  The version the rule was activated by default.
     */
    val since: String,
)
