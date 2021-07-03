package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType

/**
 * Reports when a boolean property doesn't have one of the following prefixes:
 * is|has|are.
 *
 * <noncompliant>
 * val progressBar: Boolean = true
 * </noncompliant>
 *
 * <compliant>
 * val hasProgressBar: Boolean = true
 * </compliant>
 */
@RequiresTypeResolution
class BooleanPropertyNaming(config: Config = Config.empty) : Rule(config) {

    @Configuration("naming pattern")
    private val allowedPattern by LazyRegex(
        ALLOWED_PREFIXES,
        "^(is|has|are)"
    )

    override val issue = Issue(
        javaClass.simpleName, Severity.CodeSmell,
        "Boolean property names prefix should follow the naming convention set in the projects configuration",
        Debt.FIVE_MINS
    )

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
        if (bindingContext == BindingContext.EMPTY) {
            return
        }

        val name = declaration.identifierName()
        val typeName = getTypeName(declaration)
        val isBooleanType =
            typeName == KOTLIN_BOOLEAN_TYPE_NAME || typeName == JAVA_BOOLEAN_TYPE_NAME

        if (isBooleanType && !name.contains(allowedPattern)) {
            report(reportCodeSmell(declaration, name))
        }
    }

    private fun reportCodeSmell(
        declaration: KtCallableDeclaration,
        name: String
    ): CodeSmell {
        val description = "Boolean property name should starts with $allowedPattern prefix."
        return CodeSmell(
            issue,
            Entity.from(declaration),
            message = "$description Actual name is $name"
        )
    }

    private fun getTypeName(parameter: KtCallableDeclaration): String? {
        return parameter.createTypeBindingForReturnType(bindingContext)
            ?.type
            ?.fqNameOrNull()
            .toString()
    }

    companion object {
        const val KOTLIN_BOOLEAN_TYPE_NAME = "kotlin.Boolean"
        const val JAVA_BOOLEAN_TYPE_NAME = "java.lang.Boolean"
        const val ALLOWED_PREFIXES = "allowedPrefixes"
    }
}
