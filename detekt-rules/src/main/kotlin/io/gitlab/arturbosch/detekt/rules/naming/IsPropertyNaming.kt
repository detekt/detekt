package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.js.descriptorUtils.getJetTypeFqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType

/**
 * Reports when property names inside objects which do not follow the specified naming convention are used.
 *
 * @active since v1.9.0
 */
class IsPropertyNaming(config: Config = Config.empty) : Rule(config) {

    private val kotlinBooleanTypeName = "kotlin.Boolean"
    private val javaBooleanTypeName = "java.lang.Boolean"

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Only boolean property names can start with 'is' prefix.",
        debt = Debt.FIVE_MINS
    )

    override fun visitParameter(parameter: KtParameter) {
        if (parameter.hasValOrVar()) {
            validateDeclaration(parameter)
        }

        super.visitParameter(parameter)
    }

    override fun visitProperty(property: KtProperty) {
        validateDeclaration(property)

        super.visitProperty(property)
    }

    private fun validateDeclaration(declaration: KtCallableDeclaration) {
        val name = declaration.identifierName()

        if (name.startsWith("is") && name.length > 2 && !name[2].isLowerCase()) {
            val typeName = getTypeName(declaration)

            if (typeName !== null && typeName != kotlinBooleanTypeName && typeName != javaBooleanTypeName) {
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
