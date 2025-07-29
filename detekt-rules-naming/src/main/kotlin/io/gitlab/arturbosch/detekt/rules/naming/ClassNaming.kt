package io.gitlab.arturbosch.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class or object names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
@Alias("ClassName")
class ClassNaming(config: Config) : Rule(
    config,
    "A class or object name should follow the naming convention set in detekt's configuration."
) {

    @Configuration("naming pattern")
    private val classPattern: Regex by config("[A-Z][a-zA-Z0-9]*") { it.toRegex() }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject.nameAsSafeName.isSpecial || classOrObject.nameIdentifier?.parent?.javaClass == null) {
            return
        }
        if (classOrObject.name?.matches(classPattern) != true) {
            report(
                Finding(
                    Entity.atName(classOrObject),
                    message = "Class and Object names should match the pattern: $classPattern"
                )
            )
        }
    }

    companion object {
        const val CLASS_PATTERN = "classPattern"
    }
}
