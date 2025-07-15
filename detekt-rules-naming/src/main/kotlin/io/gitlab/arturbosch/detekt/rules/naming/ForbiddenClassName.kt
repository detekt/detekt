package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class names which are forbidden per configuration. By default, this rule does not report any classes.
 * Examples for forbidden names might be too generic class names like `...Manager`.
 */
class ForbiddenClassName(config: Config) : Rule(
    config,
    "Forbidden class name as per configuration detected."
) {

    @Configuration("forbidden class names")
    private val forbiddenName: List<String> by config(emptyList<String>()) { names ->
        names.map { it.removePrefix("*").removeSuffix("*") }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val name = classOrObject.name ?: return
        val forbiddenEntries = forbiddenName.filter { name.contains(it, ignoreCase = true) }

        if (forbiddenEntries.isEmpty()) {
            return
        }

        val message = "Class name $name is forbidden as it contains: ${forbiddenEntries.joinToString(", ")}"
        report(Finding(Entity.atName(classOrObject), message))
    }
}
