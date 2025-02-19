package io.gitlab.arturbosch.detekt.api.internal

/**
 * Annotated [io.gitlab.arturbosch.detekt.api.Rule] or [io.gitlab.arturbosch.detekt.api.RuleSetProvider]
 * is auto-correctable.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoCorrectable(
    /**
     *  The detekt version since the rule is auto-correctable in the following format: <major>.<minor>.<patch>,
     *  where major, minor and patch are non-negative integer numbers.
     */
    val since: String,
)
