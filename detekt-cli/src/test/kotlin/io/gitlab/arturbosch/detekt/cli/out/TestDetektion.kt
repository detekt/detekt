package io.gitlab.arturbosch.detekt.cli.out

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

/**
 * @author Artur Bosch
 */
class TestDetektion(vararg findings: Finding) : Detektion {

	override val findings: Map<String, List<Finding>> = findings.groupBy { it.id }
	override val notifications: List<Notification> = listOf()

	override fun <V> getData(key: Key<V>): V? {
		throw UnsupportedOperationException("not implemented")
	}

	override fun <V> addData(key: Key<V>, value: V) {
		throw UnsupportedOperationException("not implemented")
	}

}
