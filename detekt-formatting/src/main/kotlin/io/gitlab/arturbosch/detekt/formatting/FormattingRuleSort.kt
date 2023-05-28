package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId

/**
 * Return a list of [FormattingRule] that respects
 * [Rule.VisitorModifier.RunAsLateAsPossible] and [Rule.VisitorModifier.RunAfterRule]
 */
@Suppress("CyclomaticComplexMethod", "UnsafeCallOnNullableType")
internal fun List<FormattingRule>.sorted(): List<FormattingRule> {
    // Initialize adjacent list graph
    data class AdjacentListNode(
        val formattingRule: FormattingRule,
        val adjacentList: MutableList<RuleId> = mutableListOf(),
        val runAsLateAsPossible: Boolean = false,
        var inDegree: Int = 0,
        var inResult: Boolean = false,
    )
    val graph = mutableMapOf<RuleId, AdjacentListNode>()

    this.forEach {
        graph[it.ktlintRuleId] = AdjacentListNode(
            formattingRule = it,
            runAsLateAsPossible = it.runAsLateAsPossible,
        )
    }
    this.forEach { formattingRule ->
        formattingRule
            .visitorModifiers
            .filterIsInstance<Rule.VisitorModifier.RunAfterRule>()
            .forEach { runAfterRule ->
                graph[runAfterRule.ruleId]!!.adjacentList.add(formattingRule.ktlintRuleId)
                graph[formattingRule.ktlintRuleId]!!.inDegree++
            }
    }

    val queue = mutableListOf<FormattingRule>()
    val result = mutableListOf<FormattingRule>()

    // First pass topological sort, considering only rules that is not run as late as possible
    this.forEach {
        if (graph[it.ktlintRuleId]!!.inDegree == 0 && it.runAsLateAsPossible.not()) {
            queue.add(it)
        }
    }

    while (queue.isNotEmpty()) {
        val formattingRule = queue.removeFirst()
        result.add(formattingRule)
        graph[formattingRule.ktlintRuleId]!!.adjacentList.forEach {
            graph[it]!!.inDegree--
            if (graph[it]!!.runAsLateAsPossible.not() && graph[it]!!.inDegree == 0) {
                queue.add(graph[it]!!.formattingRule)
            }
        }
    }

    // Second pass topological sort, including rules that should run as late as possible
    this.forEach {
        if (graph[it.ktlintRuleId]!!.inDegree == 0 && it.runAsLateAsPossible) {
            queue.add(it)
        }
    }

    while (queue.isNotEmpty()) {
        val formattingRule = queue.removeFirst()
        result.add(formattingRule)
        graph[formattingRule.ktlintRuleId]!!.adjacentList.forEach {
            graph[it]!!.inDegree--
            if (graph[it]!!.runAsLateAsPossible.not() && graph[it]!!.inDegree == 0) {
                queue.add(graph[it]!!.formattingRule)
            }
        }
    }

    return result
}

internal val FormattingRule.ktlintRuleId
    get() = wrapping.ruleId

internal val FormattingRule.visitorModifiers
    get() = wrapping.visitorModifiers

internal val FormattingRule.runAsLateAsPossible
    get() = Rule.VisitorModifier.RunAsLateAsPossible in wrapping.visitorModifiers
