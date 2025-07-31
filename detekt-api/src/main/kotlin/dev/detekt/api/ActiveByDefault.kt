package dev.detekt.api

/**
 * Annotated [Rule] or [RuleSetProvider] is active by default.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActiveByDefault(
    /**
     *  The version the rule was activated by default.
     */
    val since: String,
)
