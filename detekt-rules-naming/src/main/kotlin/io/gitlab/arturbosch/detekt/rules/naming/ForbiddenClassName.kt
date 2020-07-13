package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.valueOrDefaultCommaSeparated
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class names which are forbidden per configuration.
 * By default this rule does not report any classes.
 * Examples for forbidden names might be too generic class names like `...Manager`.
 *
 * @configuration forbiddenName - forbidden class names (default: `[]`)
 */
class ForbiddenClassName(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Forbidden class name as per configuration detected.",
            Debt.FIVE_MINS)
    private val forbiddenNames = valueOrDefaultCommaSeparated(FORBIDDEN_NAME, emptyList())
        .map { it.removePrefix("*").removeSuffix("*") }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val name = classOrObject.name
        val forbiddenEntries = name?.let { forbiddenNames.filter { name.contains(it, ignoreCase = true) } }

        if (forbiddenEntries?.isNotEmpty() == true) {
            var message = "Class name $name is forbidden as it contains:"
            forbiddenEntries.forEach { message += " $it," }
            message.trimEnd { it == ',' }

            report(CodeSmell(issue, Entity.atName(classOrObject), message))
        }
    }

    companion object {
        const val FORBIDDEN_NAME = "forbiddenName"
    }
}
