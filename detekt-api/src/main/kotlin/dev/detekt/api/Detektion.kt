package dev.detekt.api

/**
 * Storage for all kinds of findings and additional information
 * which needs to be transferred from the detekt engine to the user.
 */
class Detektion(val issues: List<Issue>, val rules: List<RuleInstance>) {
    init {
        val notReportedRules = issues.map { it.ruleInstance }.distinct().minus(rules.toSet())
        require(notReportedRules.isEmpty()) {
            if (notReportedRules.size == 1) {
                "The rule ${notReportedRules.single().id} was not reported as having been executed"
            } else {
                "The rules ${notReportedRules.map { it.id }} were not reported as having been executed"
            }
        }
    }

    private val _notifications = mutableListOf<Notification>()
    val notifications: Collection<Notification> = _notifications

    private val _metrics = mutableListOf<ProjectMetric>()
    val metrics: Collection<ProjectMetric> = _metrics

    val userData: MutableMap<String, Any> = mutableMapOf()

    fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }

    fun add(notification: Notification) {
        _notifications.add(notification)
    }
}
