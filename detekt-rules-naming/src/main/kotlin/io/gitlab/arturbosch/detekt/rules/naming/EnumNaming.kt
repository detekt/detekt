package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * Reports enum names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class EnumNaming(config: Config) : Rule(
    config,
    "Enum names should follow the naming convention set in the projects configuration."
) {

    @Configuration("naming pattern")
    private val enumEntryPattern: Regex by config("[A-Z][_a-zA-Z0-9]*") { it.toRegex() }

    override fun visitEnumEntry(enumEntry: KtEnumEntry) {
        if (!enumEntry.identifierName().matches(enumEntryPattern)) {
            report(
                CodeSmell(
                    Entity.atName(enumEntry),
                    message = "Enum entry names should match the pattern: $enumEntryPattern"
                )
            )
        }
    }

    companion object {
        const val ENUM_PATTERN = "enumEntryPattern"
    }
}
