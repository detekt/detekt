package dev.detekt.api

/**
 * Storage for all kinds of findings and additional information
 * which needs to be transferred from the detekt engine to the user.
 */
interface Detektion {
    val issues: List<Issue>
    val rules: List<RuleInstance>
    val notifications: Collection<Notification>
    val metrics: Collection<ProjectMetric>
    val userData: MutableMap<String, Any>

    /**
     * Stores a notification in the result.
     */
    fun add(notification: Notification)

    /**
     * Stores a metric calculated for the whole project in the result.
     */
    fun add(projectMetric: ProjectMetric)
}
