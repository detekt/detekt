package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class or object names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class ClassNaming(config: Config) : Rule(
    config,
    "A class or object name should fit the naming pattern defined in the projects configuration."
) {

    override val defaultRuleIdAliases: Set<String> = setOf("ClassName")

    @Configuration("naming pattern")
    private val classPattern: Regex by config("[A-Z][a-zA-Z0-9]*") { it.toRegex() }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (classOrObject.nameAsSafeName.isSpecial || classOrObject.nameIdentifier?.parent?.javaClass == null) {
            return
        }
        if (!classOrObject.identifierName().removeSurrounding("`").matches(classPattern)) {
            report(
                CodeSmell(
                    issue,
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
