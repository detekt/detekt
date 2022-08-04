package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * Composite rule which delegates work to child rules.
 * Can be used to combine different rules which do similar work like
 * scanning the source code line by line to increase performance.
 */
@Deprecated("multi rules are much more difficult to maintain and support will be removed in the future")
@Suppress("RequiresTypeResolution")
abstract class MultiRule : BaseRule() {

    abstract val rules: List<Rule>
    var activeRules: Set<Rule> by SingleAssign()

    override fun visitCondition(root: KtFile): Boolean = true

    override fun preVisit(root: KtFile) {
        activeRules = rules.filterTo(HashSet(rules.size)) { it.visitCondition(root) }
        activeRules.forEach { singleRule ->
            singleRule.bindingContext = this.bindingContext
        }
    }

    override fun postVisit(root: KtFile) {
        for (activeRule in activeRules) {
            report(activeRule.findings, activeRule.aliases, activeRule.ruleSetId)
        }
    }

    /**
     * Preferred way to run child rules because this composite rule
     * takes care of evaluating if a specific child should be run at all.
     */
    fun <T : Rule> T.runIfActive(block: T.() -> Unit) {
        if (this in activeRules) {
            block()
        }
    }
}
