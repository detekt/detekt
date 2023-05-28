package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleId

/**
 * Return a list of [FormattingRule] that respects
 * [Rule.VisitorModifier.RunAsLateAsPossible] and [Rule.VisitorModifier.RunAfterRule]
 */
@Suppress("CyclomaticComplexMethod")
internal fun List<FormattingRule>.sorted(): List<FormattingRule> {
    // Initialize
    val graph = buildAdjacentListGraph(this)
    val queue = mutableListOf<FormattingRule>()
    val result = mutableListOf<FormattingRule>()

    // First pass topological sort, considering only rules that is not run as late as possible
    topologySort(
        formattingRules = this,
        queue = queue,
        graph = graph,
        result = result,
        init = { it.runAsLateAsPossible.not() },
        enqueue = {
            if (it.runAsLateAsPossible.not()) {
                queue.add(it)
            }
        },
    )

    // Second pass topological sort, including rules that should run as late as possible
    topologySort(
        formattingRules = this,
        queue = queue,
        graph = graph,
        result = result,
        init = { it.runAsLateAsPossible },
        enqueue = {
            if (it.runAsLateAsPossible) {
                queue.add(it)
            } else {
                queue.add(0, it)
            }
        },
    )
    return result
}

private fun buildAdjacentListGraph(
    formattingRules: List<FormattingRule>,
): Map<RuleId, AdjacentListNode> {
    val graph = mutableMapOf<RuleId, AdjacentListNode>()
    formattingRules.forEach {
        graph[it.wrappingRuleId] = AdjacentListNode(
            formattingRule = it
        )
    }
    formattingRules.forEach { formattingRule ->
        formattingRule
            .visitorModifiers
            .filterIsInstance<Rule.VisitorModifier.RunAfterRule>()
            .forEach { runAfterRule ->
                graph[runAfterRule.ruleId]!!.adjacentList.add(formattingRule.wrappingRuleId)
                graph[formattingRule.wrappingRuleId]!!.inDegree++
            }
    }
    return graph
}

@Suppress("NestedBlockDepth", "LongParameterList")
private fun topologySort(
    formattingRules: List<FormattingRule>,
    queue: MutableList<FormattingRule>,
    graph: Map<RuleId, AdjacentListNode>,
    result: MutableList<FormattingRule>,
    init: (FormattingRule) -> Boolean,
    enqueue: (FormattingRule) -> Unit,
) {
    formattingRules.forEach {
        if (graph[it.wrappingRuleId]?.inDegree == 0 && init(it)) {
            queue.add(it)
        }
    }

    while (queue.isNotEmpty()) {
        val formattingRule = queue.removeFirst()
        result.add(formattingRule)
        graph[formattingRule.wrappingRuleId]?.adjacentList?.forEach { ruleId ->
            graph[ruleId]?.let { adjacentListNode ->
                adjacentListNode.inDegree--
                if (adjacentListNode.inDegree == 0)
                    enqueue(adjacentListNode.formattingRule)
            }
        }
    }
}

/**
 * Adjacent list graph
 */
private data class AdjacentListNode(
    val formattingRule: FormattingRule,
    val adjacentList: MutableList<RuleId> = mutableListOf(),
    var inDegree: Int = 0,
)

internal val FormattingRule.wrappingRuleId
    get() = wrapping.ruleId

internal val FormattingRule.visitorModifiers
    get() = wrapping.visitorModifiers

internal val FormattingRule.runAsLateAsPossible
    get() = Rule.VisitorModifier.RunAsLateAsPossible in wrapping.visitorModifiers
