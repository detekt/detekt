package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Finding

/**
 * @author Artur Bosch
 */
interface Detektion {
	val findings: Map<String, List<Finding>>
	val notifications: List<Notification>
}

data class DetektResult(override val findings: Map<String, List<Finding>>,
						override val notifications: List<Notification>) : Detektion