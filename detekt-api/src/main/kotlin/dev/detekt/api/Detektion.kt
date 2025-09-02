package dev.detekt.api

/**
 * Storage for all kinds of findings and additional information
 * which needs to be transferred from the detekt engine to the user.
 */
class Detektion(
    val issues: List<Issue>,
    val rules: List<RuleInstance>,
    val notifications: List<Notification> = emptyList(),
    val metrics: List<ProjectMetric> = emptyList(),
    val userData: MutableMap<String, Any> = mutableMapOf(),
) {
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
    fun plus(projectMetric: ProjectMetric): Detektion = this.copy(metrics = metrics + projectMetric)

    fun plus(notification: Notification): Detektion = this.copy(notifications = notifications + notification)

    private fun copy(
        issues: List<Issue> = this.issues,
        rules: List<RuleInstance> = this.rules,
        notifications: List<Notification> = this.notifications,
        metrics: List<ProjectMetric> = this.metrics,
        userData: Map<String, Any> = this.userData,
    ) = Detektion(issues, rules, notifications, metrics, userData.toMutableMap())
}
