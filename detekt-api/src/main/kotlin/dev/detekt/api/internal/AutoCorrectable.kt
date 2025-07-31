package dev.detekt.api.internal

/**
 * Annotated [dev.detekt.api.Rule] or [dev.detekt.api.RuleSetProvider]
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
