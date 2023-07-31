package io.gitlab.arturbosch.detekt.api.internal

/**
 * Annotated [io.gitlab.arturbosch.detekt.api.Rule] or [io.gitlab.arturbosch.detekt.api.RuleSetProvider]
 * is active by default.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActiveByDefault(
    /**
     *  The detekt version the rule was activated by default in the following format: <major>.<minor>.<patch>,
     *  where major, minor and patch are non-negative integer numbers.
     */
    val since: String
)
