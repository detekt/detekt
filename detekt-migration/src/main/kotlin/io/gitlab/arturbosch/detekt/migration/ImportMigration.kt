package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric

/**
 * @author Artur Bosch
 */
data class ImportMigration(private val toReplace: String,
						   private val replacement: String,
						   override val entity: Entity) : Finding {
	override val references: List<Entity> = emptyList()
	override val metrics: List<Metric> = emptyList()
	override val issue: Issue = Issue("ImportMigration", Issue.Severity.Defect)
	override val message = "${issue.id} - $toReplace migrated to $replacement @ ${entity.location.compact()}"
	override fun compact(): String = issue.description
}