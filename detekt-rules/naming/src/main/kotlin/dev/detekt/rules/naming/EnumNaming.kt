package dev.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtEnumEntry

/**
 * Reports enum names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
@Alias("EnumEntryName")
class EnumNaming(config: Config) :
    Rule(config, "Enum names should follow the naming convention set in detekt's configuration.") {

    @Configuration("naming pattern")
    private val enumEntryPattern: Regex by config("[A-Z][_a-zA-Z0-9]*") { it.toRegex() }

    override fun visitEnumEntry(enumEntry: KtEnumEntry) {
        if (enumEntry.name?.matches(enumEntryPattern) != true) {
            report(
                Finding(
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
