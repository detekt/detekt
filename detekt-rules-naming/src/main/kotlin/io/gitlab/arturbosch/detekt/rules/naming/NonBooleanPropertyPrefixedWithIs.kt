package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.builtins.isFunctionOrKFunctionTypeWithAnySuspendability
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.typeBinding.createTypeBindingForReturnType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError

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
class NonBooleanPropertyPrefixedWithIs(config: Config) :
    Rule(
        config,
        "Only boolean property names can start with `is` prefix."
    ),
    RequiresTypeResolution {
    private val booleanTypes = listOf(
        "kotlin.Boolean",
        "java.lang.Boolean",
        "java.util.concurrent.atomic.AtomicBoolean",
    ).map { FqName(it) }.toSet()

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
        if (name.startsWith("is") && name.getOrNull(2)?.isUpperCase() == true) {
            val (type, typeFqName) = getType(declaration) ?: return
            if (!type.isBoolean()) {
                report(declaration, name, typeFqName)
            }
        }
    }

    private fun getType(parameter: KtCallableDeclaration): Pair<KotlinType, FqName>? {
        val type = parameter.createTypeBindingForReturnType(bindingContext)?.type ?: return null
        if (type.isError) return null
        val fqName = type.fqNameOrNull() ?: return null
        return type to fqName
    }

    private fun KotlinType.isBoolean(): Boolean =
        if (isFunctionOrKFunctionTypeWithAnySuspendability) {
            arguments.lastOrNull()?.type?.fqNameOrNull() in booleanTypes
        } else {
            fqNameOrNull() in booleanTypes
        }

    private fun report(declaration: KtCallableDeclaration, name: String, typeFqName: FqName) {
        val typeName = typeFqName.shortName().asString()
        val codeSmell = CodeSmell(
            Entity.from(declaration),
            message = "Non-boolean properties shouldn't start with 'is' prefix. Actual type of $name: $typeName"
        )
        report(codeSmell)
    }
}
