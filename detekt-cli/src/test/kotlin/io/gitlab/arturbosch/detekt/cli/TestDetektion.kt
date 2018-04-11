package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

/**
 * @author Artur Bosch
 * @author schalkms
 */
open class TestDetektion(vararg findings: Finding) : Detektion {

	override val metrics: Collection<ProjectMetric> = listOf()
	override val findings: Map<String, List<Finding>> = findings.groupBy { it.id }
	override val notifications: List<Notification> = listOf()

	override fun add(notification: Notification) = throw UnsupportedOperationException("not implemented")
	override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException("not implemented")

	override fun <V> getData(key: Key<V>) = throw UnsupportedOperationException("not implemented")
	override fun <V> addData(key: Key<V>, value: V) = throw UnsupportedOperationException("not implemented")
}
