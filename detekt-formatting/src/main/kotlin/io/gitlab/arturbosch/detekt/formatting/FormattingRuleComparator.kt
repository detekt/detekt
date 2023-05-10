package io.gitlab.arturbosch.detekt.formatting

/**
 * This serves as weak heuristic to order the wrapped rules according to their visitor modifiers.
 * Currently only RunAsLateAsPossible is supported.
 */
internal object FormattingRuleComparator : Comparator<FormattingRule> {
    override fun compare(o1: FormattingRule, o2: FormattingRule): Int {
        if (o1.runAsLateAsPossible == o2.runAsLateAsPossible) {
            return 0
        }
        return if (o1.runAsLateAsPossible) 1 else -1
    }

}
