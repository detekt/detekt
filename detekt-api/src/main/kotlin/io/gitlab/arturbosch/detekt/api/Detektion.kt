package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolder

/**
 * Storage for all kinds of findings and additional information
 * which needs to be transferred from the detekt engine to the user.
 */
interface Detektion : UserDataHolder {
    val findings: Map<RuleSetId, List<Finding>>
    val notifications: Collection<Notification>
    val metrics: Collection<ProjectMetric>

    /**
     * Stores a notification in the result.
     */
    fun add(notification: Notification)

    /**
     * Stores a metric calculated for the whole project in the result.
     */
    fun add(projectMetric: ProjectMetric)
}
