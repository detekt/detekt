package dev.detekt.api

import com.intellij.openapi.util.UserDataHolder

/**
 * Storage for all kinds of findings and additional information
 * which needs to be transferred from the detekt engine to the user.
 */
interface Detektion : UserDataHolder {
    val issues: List<Issue>
    val rules: List<RuleInstance>
    val metrics: Collection<ProjectMetric>

    /**
     * Stores a metric calculated for the whole project in the result.
     */
    fun add(projectMetric: ProjectMetric)
}
