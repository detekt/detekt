package io.gitlab.arturbosch.detekt.api

/**
 * Debt describes the estimated amount of work needed to fix a given issue.
 */
@Suppress("MagicNumber")
data class Debt(val days: Int = 0, val hours: Int = 0, val mins: Int = 0) {

    init {
        require(days >= 0 && hours >= 0 && mins >= 0)
        require(!(days == 0 && hours == 0 && mins == 0))
    }

    /**
     * Adds the other debt to this debt.
     * This recalculates the potential overflow resulting from the addition.
     */
    operator fun plus(other: Debt): Debt {
        var minutes = mins + other.mins
        var hours = hours + other.hours
        var days = days + other.days
        hours += minutes / MINUTES_PER_HOUR
        minutes %= MINUTES_PER_HOUR
        days += hours / HOURS_PER_DAY
        hours %= HOURS_PER_DAY
        return Debt(days, hours, minutes)
    }

    companion object {
        val TWENTY_MINS: Debt =
            Debt(0, 0, 20)
        val TEN_MINS: Debt =
            Debt(0, 0, 10)
        val FIVE_MINS: Debt =
            Debt(0, 0, 5)
        private const val HOURS_PER_DAY = 24
        private const val MINUTES_PER_HOUR = 60
    }

    override fun toString(): String {
        return with(StringBuilder()) {
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (mins > 0) append("${mins}min")
            toString()
        }.trimEnd()
    }
}
