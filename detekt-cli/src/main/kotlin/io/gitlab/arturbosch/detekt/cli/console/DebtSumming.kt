package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Finding

class DebtSumming() {

    private val debtList = mutableListOf<Debt>()

    constructor(issues: List<Finding>) : this() {
        issues.forEach { debtList.add(it.issue.debt) }
    }

    fun add(debt: Debt) {
        debtList.add(debt)
    }

    fun calculateDebt(): Debt {
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
