package io.gitlab.arturbosch.detekt.api

/**
 * The [Suppressor]s are a tool that you can use to customize the reports of detekt. They allow you
 * to(surprise) suppress some issues detected by some rules, and thy can be applied to any rule.
 */
fun interface Suppressor {
    /**
     * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
     */
    fun shouldSuppress(finding: Finding): Boolean
}
