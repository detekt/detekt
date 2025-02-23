package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.isConstant
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Reports property names inside objects that do not follow the specified naming convention.
 */
@ActiveByDefault(since = "1.0.0")
@Alias("ObjectPropertyName")
class ObjectPropertyNaming(config: Config) : Rule(
    config,
    "Property names inside objects should follow the naming convention set in detekt's configuration."
) {

    @Configuration("naming pattern")
    private val constantPattern: Regex by config("[A-Za-z][_A-Za-z0-9]*") { it.toRegex() }

    @Configuration("naming pattern")
    private val propertyPattern: Regex by config("[A-Za-z][_A-Za-z0-9]*") { it.toRegex() }

    @Configuration("naming pattern")
    private val privatePropertyPattern: Regex by config("(_)?[A-Za-z][_A-Za-z0-9]*") { it.toRegex() }

    override fun visitProperty(property: KtProperty) {
        if (property.isPropertyOfObjectDeclaration().not()) {
            return
        }

        if (property.isConstant()) {
            handleConstant(property)
        } else {
            handleProperty(property)
        }
    }

    private fun KtProperty.isPropertyOfObjectDeclaration(): Boolean =
        this.isMember && this.getNonStrictParentOfType<KtClassOrObject>() is KtObjectDeclaration

    private fun handleConstant(property: KtProperty) {
        if (property.name?.matches(constantPattern) != true) {
            report(property, "Object constant names should match the pattern: $constantPattern")
        }
    }

    private fun handleProperty(property: KtProperty) {
        if (property.isPrivate()) {
            if (property.name?.matches(privatePropertyPattern) != true) {
                report(property, "Private object property names should match the pattern: $privatePropertyPattern")
            }
        } else if (property.name?.matches(propertyPattern) != true) {
            report(property, "Object property names should match the pattern: $propertyPattern")
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
}
