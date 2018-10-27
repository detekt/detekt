package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId
import io.gitlab.arturbosch.detekt.cli.baseline.BaselineFacade

/**
 * @author Artur Bosch
 */
class FilteredDetectionResult(detektion: Detektion, baselineFacade: BaselineFacade) : Detektion by detektion {

	private val filteredFindings: Map<RuleSetId, List<Finding>>

	init {
		filteredFindings = detektion.findings
				.map { (key, value) -> key to baselineFacade.filter(value) }
				.toMap()
	}

	override val findings = filteredFindings
}
