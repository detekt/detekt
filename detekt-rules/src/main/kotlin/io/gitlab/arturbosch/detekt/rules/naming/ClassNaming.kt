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
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports when class or object names which do not follow the specified naming convention are used.
 *
 * @configuration classPattern - naming pattern (default: `'[A-Z][a-zA-Z0-9]*'`)
 * @active since v1.0.0
 */
class ClassNaming(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "A class or object's name should fit the naming pattern defined in the projects configuration.",
            debt = Debt.FIVE_MINS)
    private val classPattern by LazyRegex(CLASS_PATTERN, "[A-Z][a-zA-Z0-9]*")

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        if (!classOrObject.identifierName().matches(classPattern)) {
            report(CodeSmell(
                    issue,
                    Entity.atName(classOrObject),
                    message = "Class and Object names should match the pattern: $classPattern"))
        }
    }

    companion object {
        const val CLASS_PATTERN = "classPattern"
    }
}
