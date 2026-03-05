package dev.detekt.generator.printer.defaultconfig

import dev.detekt.generator.collection.Rule

/**
 * Holds a list of default `ignoreAnnotated` values for rules.
 */
val ignoreAnnotatedDefaults: Array<IgnoreAnnotated> = arrayOf(TestIgnoreAnnotated)

/**
 * Tracks rules which need an extra `ignoreAnnotated` property when printing the default detekt config yaml file.
 */
abstract class IgnoreAnnotated {
    abstract val annotations: List<String>
    abstract val rules: Set<String>

    fun getAnnotations(rule: Rule): List<String>? = if (rule.name in rules) annotations else null
}

private object TestIgnoreAnnotated : IgnoreAnnotated() {
    override val annotations = listOf("Test")
    override val rules = setOf("ImplicitUnitReturnType")
}
