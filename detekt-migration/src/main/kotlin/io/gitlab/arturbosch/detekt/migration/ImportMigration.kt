package io.gitlab.arturbosch.detekt.migration

import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity

/**
 * @author Artur Bosch
 */
data class ImportMigration(private val toReplace: String,
						   private val replacement: String,
						   override val entity: Entity) : Finding {
	override val id: String = "ImportMigration"
	override val issue: Issue = Issue(id, Severity.Minor,
			"$id - $toReplace migrated to $replacement @ ${entity.location.compact()}")
	override val references: List<Entity> = emptyList()
	override val metrics: List<Metric> = emptyList()
	override fun compact(): String = issue.description
}