package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.isConstant
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType

/**
 * Reports when a boolean property doesn't match a pattern
 *
 * <noncompliant>
 * val progressBar: Boolean = true
 * </noncompliant>
 *
 * <compliant>
 * val hasProgressBar: Boolean = true
 * </compliant>
 */
class BooleanPropertyNaming(config: Config) :
    Rule(
        config,
        "Boolean property name should follow the naming convention set in the projects configuration."
    ),
    RequiresTypeResolution {
    @Configuration("naming pattern")
    private val allowedPattern: Regex by config("^(is|has|are)", String::toRegex)

    @Configuration("ignores properties that have the override modifier")
    @Deprecated("This configuration is ignored and will be removed in the future")
    @Suppress("unused")
    private val ignoreOverridden: Boolean by config(true)

    override fun visitParameter(parameter: KtParameter) {
        super.visitParameter(parameter)

        if (parameter.hasValOrVar()) {
            validateDeclaration(parameter)
        }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        validateDeclaration(property)
    }

    private fun validateDeclaration(declaration: KtCallableDeclaration) {
        val name = declaration.name ?: return
        val typeName = getTypeName(declaration)
        val isBooleanType =
            typeName == KOTLIN_BOOLEAN_TYPE_NAME || typeName == JAVA_BOOLEAN_TYPE_NAME
        val isNonConstantBooleanType = isBooleanType && !declaration.isConstant()

        if (isNonConstantBooleanType && !name.contains(allowedPattern) && !declaration.isOverride()) {
            report(reportCodeSmell(declaration, name))
        }
    }

    private fun reportCodeSmell(
        declaration: KtCallableDeclaration,
        name: String
    ): CodeSmell {
        val description = "Boolean property name should match a $allowedPattern pattern."
        return CodeSmell(
            Entity.atName(declaration),
            message = "$description Actual name is $name"
        )
    }

    private fun getTypeName(parameter: KtCallableDeclaration): String =
        parameter.createTypeBindingForReturnType(bindingContext)
            ?.type
            ?.fqNameOrNull()
            .toString()

    companion object {
        const val KOTLIN_BOOLEAN_TYPE_NAME = "kotlin.Boolean"
        const val JAVA_BOOLEAN_TYPE_NAME = "java.lang.Boolean"
    }
}
