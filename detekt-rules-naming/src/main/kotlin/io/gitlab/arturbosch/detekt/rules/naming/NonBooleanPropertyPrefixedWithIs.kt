package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.identifierName
import org.jetbrains.kotlin.builtins.isFunctionOrKFunctionTypeWithAnySuspendability
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isBoolean

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
 */
@RequiresTypeResolution
class NonBooleanPropertyPrefixedWithIs(config: Config = Config.empty) : Rule(config) {

    private val kotlinBooleanTypeName = "kotlin.Boolean"
    private val javaBooleanTypeName = "java.lang.Boolean"

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Only boolean property names can start with `is` prefix.",
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
        val name = declaration.identifierName()

        if (name.startsWith("is") && name.length > 2 && !name[2].isLowerCase()) {
            val type = getType(declaration)
            val typeName = type?.getTypeName()
            val isNotBooleanType = typeName != kotlinBooleanTypeName && typeName != javaBooleanTypeName

            if (!typeName.isNullOrEmpty() &&
                isNotBooleanType &&
                !type.isBooleanFunction()
            ) {
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

    private fun getType(parameter: KtCallableDeclaration): KotlinType? =
        parameter.createTypeBindingForReturnType(bindingContext)
            ?.type

    private fun KotlinType.getTypeName(): String? =
        fqNameOrNull()
            ?.asString()

    private fun KotlinType.isBooleanFunction(): Boolean {
        if (!isFunctionOrKFunctionTypeWithAnySuspendability) return false

        return arguments.isNotEmpty() && arguments.last().type.isBoolean()
    }
}
