package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Rule

/**
 * @author Artur Bosch
 */
data class ImportMigration(private val toReplace: String,
						   private val replacement: String,
						   override val entity: Entity) : Finding {
	override val id: String = "ImportMigration"
	override val severity: Rule.Severity = Rule.Severity.Minor
	override val references: List<Entity> = emptyList()
	override val metrics: List<Metric> = emptyList()
	override val description: String = "$id - $toReplace migrated to $replacement @ ${entity.location.compact()}"
	override fun compact(): String = description
}