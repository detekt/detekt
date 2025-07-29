package dev.detekt.api

/**
 * Provides alias for the annotated rule
 *
 * The aliases are commonly used for suppress the rule with different names
 *
 * @property values the aliases of the annotated rule
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Alias(
    vararg val values: String,
)
