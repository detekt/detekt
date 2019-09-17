package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

class DebtSumming {

    private val debtList = mutableListOf<Debt>()

    fun add(debt: Debt) {
        debtList.add(debt)
    }

    fun calculateDebt(): Debt? {
        if (debtList.isEmpty()) {
            return null
        }
        return calculate()
    }

    fun printDebtInformation(
        issues: Map<RuleSetId, List<Finding>>,
        totalDebt: DebtSumming
    ): String? {
        with(StringBuilder()) {
            issues
                .filter { it.value.isNotEmpty() }
                .forEach { (ruleSetId, issues) ->
                    val debtSumming = DebtSumming()
                    val issuesString = issues.joinToString("") {
                        debtSumming.add(it.issue.debt)
                        it.compact().format("\t")
                    }
                    val debt = debtSumming.calculateDebt()
                    val debtString =
                        if (debt != null) {
                            totalDebt.add(debt)
                            " - $debt debt".format()
                        } else {
                            "\n"
                        }
                    append(ruleSetId.format(prefix = "Ruleset: ", suffix = debtString))
                    append(issuesString.yellow())
                }
            return toString()
        }
    }

    fun printFileBasedDebtInformation(
        issues: Map<RuleSetId, List<Finding>>,
        fileDebt: DebtSumming,
        totalDebt: DebtSumming
    ): String? {
        with(StringBuilder()) {
            issues
                .filter { it.value.isNotEmpty() }
                .forEach { (_, issues) ->
                    val debtSumming = DebtSumming()
                    val issuesString = issues.joinToString("") {
                        debtSumming.add(it.issue.debt)
                        it.compact().format("\t")
                    }
                    val debt = debtSumming.calculateDebt()
                    debt?.let {
                        fileDebt.add(debt)
                        totalDebt.add(debt)
                    } ?: append("\n")
                    append(issuesString.yellow())
                }
            return toString()
        }
    }

    private fun calculate(): Debt {
        var minutes = 0
        var hours = 0
        var days = 0
        debtList.forEach {
            minutes += it.mins
            hours += it.hours
            days += it.days
        }
        hours += minutes / MINUTES_PER_HOUR
        minutes %= MINUTES_PER_HOUR
        days += hours / HOURS_PER_DAY
        hours %= HOURS_PER_DAY
        return Debt(days, hours, minutes)
    }

    companion object {
        private const val HOURS_PER_DAY = 24
        private const val MINUTES_PER_HOUR = 60
    }
}
