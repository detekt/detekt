package io.gitlab.arturbosch.detekt.api

fun Finding.toFinding2(): Finding2 {
    return when (this) {
        is CorrectableCodeSmell -> CorrectableCodeSmell2(issue, entity, message, references, autoCorrectEnabled).also {
            it.internalSeverity = severity
        }

        is CodeSmell -> CodeSmell2(issue, entity, message, references).also {
            it.internalSeverity = severity
        }

        else -> error("wtf?")
    }
}

open class CodeSmell2(
    final override val issue: Issue,
    final override val entity: Entity,
    final override val message: String,
    final override val references: List<Entity> = emptyList()
) : Finding2 {
    init {
        require(message.isNotBlank()) { "The message should not be empty" }
    }

    var internalSeverity: Severity? = null
    override val severity: Severity
        get() = internalSeverity ?: super.severity

    override fun compact(): String = "${issue.id} - ${entity.compact()}"

    override fun compactWithSignature(): String = compact() + " - Signature=" + entity.signature

    override fun toString(): String {
        return "CodeSmell(issue=$issue, " +
            "entity=$entity, " +
            "message=$message, " +
            "references=$references, " +
            "severity=$severity)"
    }
}

open class CorrectableCodeSmell2(
    issue: Issue,
    entity: Entity,
    message: String,
    references: List<Entity> = emptyList(),
    val autoCorrectEnabled: Boolean
) : CodeSmell2(
    issue,
    entity,
    message,
    references
) {
    override fun toString(): String {
        return "CorrectableCodeSmell(" +
            "autoCorrectEnabled=$autoCorrectEnabled, " +
            "issue=$issue, " +
            "entity=$entity, " +
            "message=$message, " +
            "references=$references, " +
            "severity=$severity)"
    }
}
