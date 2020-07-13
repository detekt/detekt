package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * Reports when enum names which do not follow the specified naming convention are used.
 *
 * @configuration enumEntryPattern - naming pattern (default: `'[A-Z][_a-zA-Z0-9]*'`)
 * @active since v1.0.0
 */
class EnumNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Enum names should follow the naming convention set in the projects configuration.",
            debt = Debt.FIVE_MINS)

    private val enumEntryPattern by LazyRegex(ENUM_PATTERN, "[A-Z][_a-zA-Z0-9]*")

    override fun visitEnumEntry(enumEntry: KtEnumEntry) {
        if (!enumEntry.identifierName().matches(enumEntryPattern)) {
            report(CodeSmell(
                    issue,
                    Entity.from(enumEntry.nameIdentifier ?: enumEntry),
                    message = "Enum entry names should match the pattern: $enumEntryPattern"))
        }
    }

    companion object {
        const val ENUM_PATTERN = "enumEntryPattern"
    }
}
