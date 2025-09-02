package dev.detekt.api.testfixtures

import com.intellij.openapi.util.Key
import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance

@Suppress("FunctionNaming")
fun TestDetektion(
    vararg issues: Issue,
    rules: List<RuleInstance> = emptyList(),
    notifications: List<Notification> = emptyList(),
    metrics: List<ProjectMetric> = emptyList(),
    userData: Map<String, Any> = emptyMap(),
): Detektion = Detektion(
    issues = issues.toList(),
    rules = rules,
    notifications = notifications,
    metrics = metrics,
    userData = userData.toMutableMap()
)

fun <V> Detektion.removeData(key: Key<V>) {
    userData.remove(key.toString())
}
