package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType

/**
 * Reports when property with 'is' prefix doesn't have a boolean type.
 * Please check the [chapter 8.3.2 at Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.2)
 *
 * <noncompliant>
 * val isEnabled: Int = 500
 * </noncompliant>
 *
 * <compliant>
 * val isEnabled: Boolean = false
 * </compliant>
 *
 * @since 1.12.0
 * @requiresTypeResolution
 */
class NonBooleanPropertyPrefixedWithIs(config: Config = Config.empty) : Rule(config) {

    private val kotlinBooleanTypeName = "kotlin.Boolean"
    private val javaBooleanTypeName = "java.lang.Boolean"

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Only boolean property names can start with 'is' prefix.",
        debt = Debt.FIVE_MINS
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

        if (name.startsWith("is") && name.length > 2 && !name[2].isLowerCase()) {
            val typeName = getTypeName(declaration)

            if (typeName != null && typeName != kotlinBooleanTypeName && typeName != javaBooleanTypeName) {
                report(
                    reportCodeSmell(declaration, name, typeName)
                )
            }
        }
    }

    private fun reportCodeSmell(
        declaration: KtCallableDeclaration,
        name: String,
        typeName: String
    ): CodeSmell {
        return CodeSmell(
            issue,
            Entity.from(declaration),
            message = "Non-boolean properties shouldn't start with 'is' prefix. Actual type of $name: $typeName"
        )
    }

    private fun getTypeName(parameter: KtCallableDeclaration): String? {
        return parameter.createTypeBindingForReturnType(bindingContext)
            ?.type
            ?.getJetTypeFqName(false)
    }
}
