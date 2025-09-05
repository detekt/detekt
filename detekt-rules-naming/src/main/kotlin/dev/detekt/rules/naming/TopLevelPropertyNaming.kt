package dev.detekt.rules.naming

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isConstant
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isExtensionDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports top level property names that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
class TopLevelPropertyNaming(config: Config) : Rule(
    config,
    "Top level property names should follow the naming convention set in detekt's configuration."
) {

    @Configuration("naming pattern")
    private val constantPattern: Regex by config("[A-Z][_A-Z0-9]*") { it.toRegex() }

    @Configuration("naming pattern")
    private val propertyPattern: Regex by config("[A-Za-z][_A-Za-z0-9]*") { it.toRegex() }

    @Configuration("naming pattern")
    private val privatePropertyPattern: Regex by config("_?[A-Za-z][_A-Za-z0-9]*") { it.toRegex() }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (!property.isTopLevel || property.psiOrParent.isExtensionDeclaration()) return

        if (property.isConstant()) {
            handleConstant(property)
        } else {
            handleProperty(property)
        }
    }

    private fun handleConstant(property: KtProperty) {
        if (property.name?.matches(constantPattern) != true) {
            report(property, "Top level constant names should match the pattern: $constantPattern")
        }
    }

    private fun handleProperty(property: KtProperty) {
        if (property.isPrivate()) {
            if (property.name?.matches(privatePropertyPattern) != true) {
                report(property, "Private top level property names should match the pattern: $privatePropertyPattern")
            }
        } else if (property.name?.matches(propertyPattern) != true) {
            report(property, "Top level property names should match the pattern: $propertyPattern")
        }
    }

    private fun report(property: KtProperty, message: String) {
        report(
            Finding(
                Entity.atName(property),
                message = message
            )
        )
    }

    companion object {
        const val CONSTANT_PATTERN = "constantPattern"
    }
}
