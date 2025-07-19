package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Reports when property with 'is' prefix doesn't have a boolean type.
 * Please check [chapter 8.3.2 on the Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.2)
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
    RequiresAnalysisApi {

    private val booleanTypes = listOf(
        "kotlin.Boolean",
        "java.lang.Boolean",
        "java.util.concurrent.atomic.AtomicBoolean",
    ).map { ClassId.fromString(it.replace('.', '/')) }.toSet()

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
            analyze(declaration) {
                val type = declaration.returnType
                val typeSymbol = type.symbol
                if (typeSymbol != null && !type.isBoolean()) {
                    val typeName = typeSymbol.classId?.shortClassName?.asString() ?: "Unknown"
                    report(declaration, name, typeName)
                }
            }
        }
    }

    private fun KaType.isBoolean(): Boolean {
        if (symbol?.classId in booleanTypes) return true
        // Otherwise, we check if the type is a function type that returns a Boolean
        val classIdString = symbol?.classId?.asSingleFqName()?.asString()
        if (classIdString != null && isFunctionTypeName(classIdString)) {
            // For function types, the last type argument is the return type
            val typeArguments = (this as? KaClassType)?.typeArguments
            if (!typeArguments.isNullOrEmpty()) {
                val returnType = typeArguments.last().type
                if (returnType != null) {
                    val returnTypeClassId = returnType.symbol?.classId
                    return returnTypeClassId in booleanTypes
                }
            }
        }
        return false
    }

    // Checks for either regular function, suspending function, or function references.
    private fun isFunctionTypeName(classIdString: String): Boolean = classIdString.startsWith("kotlin.Function") ||
        classIdString.startsWith("kotlin.coroutines.SuspendFunction") ||
        classIdString.startsWith("kotlin.reflect.KFunction")

    private fun report(declaration: KtCallableDeclaration, name: String, typeName: String) {
        val finding = Finding(
            Entity.from(declaration),
            message = "Non-boolean properties shouldn't start with 'is' prefix. Actual type of $name: $typeName"
        )
        report(finding)
    }
}
