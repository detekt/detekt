package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class names which are forbidden per configuration.
 * By default this rule does not report any classes.
 * Examples for forbidden names might be too generic class names like `...Manager`.
 */
class ForbiddenClassName(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Forbidden class name as per configuration detected.",
        Debt.FIVE_MINS
    )

    @Configuration("forbidden class names")
    private val forbiddenName: List<String> by config(listOf<String>()) { names ->
        names.map { it.removePrefix("*").removeSuffix("*") }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val name = classOrObject.name
        val forbiddenEntries = name?.let { forbiddenName.filter { name.contains(it, ignoreCase = true) } }

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
