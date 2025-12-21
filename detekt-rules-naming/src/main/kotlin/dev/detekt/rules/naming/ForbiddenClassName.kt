package dev.detekt.rules.naming

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.simplePatternToRegex
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports class names which are forbidden per configuration. By default, this rule does not report any classes.
 * This can be used to prevent the use of overly generic class names like `*Manager` or names shadowing common
 * types like `LocalDate`.
 */
class ForbiddenClassName(config: Config) : Rule(config, "Forbidden class name as per configuration detected.") {

    @Configuration("List of glob patterns to be disallowed as class names")
    private val forbiddenName: List<Regex> by config(emptyList<String>()) { patterns ->
        patterns.map(String::simplePatternToRegex)
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        val name = classOrObject.name ?: return
        val forbiddenEntries = forbiddenName.filter { name.matches(it) }

        if (forbiddenEntries.isEmpty()) {
            return
        }

        val message = "Class name $name is forbidden as it matches: ${forbiddenEntries.joinToString(", ")}"
        report(Finding(Entity.atName(classOrObject), message))
    }
}
