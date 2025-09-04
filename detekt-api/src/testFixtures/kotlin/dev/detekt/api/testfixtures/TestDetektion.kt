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
    metrics: List<ProjectMetric> = emptyList(),
    notifications: List<Notification> = emptyList(),
    userData: Map<String, Any> = emptyMap(),
): Detektion = Detektion(
    issues.toList(),
    rules,
).apply {
    metrics.forEach { add(it) }
    notifications.forEach { add(it) }
    this.userData.putAll(userData)
}

fun <V> Detektion.removeData(key: Key<V>) {
    userData.remove(key.toString())
}
