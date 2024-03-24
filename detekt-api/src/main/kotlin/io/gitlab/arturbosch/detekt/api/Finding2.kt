package io.gitlab.arturbosch.detekt.api

/**
 * Represents a problem detected by detekt on the source code
 *
 * A finding has information about the rule that detected the problem, a severity and an entity with information
 * about the position. Entity references can also be considered for deeper characterization.
 */
interface Finding2 {
    val ruleInfo: RuleInfo
    val entity: Entity
    val references: List<Entity>
    val message: String
    val severity: Severity
    val autoCorrectEnabled: Boolean

    val location: Location
        get() = entity.location

    interface RuleInfo {
        val id: Rule.Id
        val ruleSetId: RuleSet.Id
        val description: String
    }
}
