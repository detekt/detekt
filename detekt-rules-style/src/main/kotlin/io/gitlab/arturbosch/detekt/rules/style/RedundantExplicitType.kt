package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.AbbreviatedType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isChar
import org.jetbrains.kotlin.types.typeUtil.isDouble
import org.jetbrains.kotlin.types.typeUtil.isFloat
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isLong

/*
 * Based on code from Kotlin compiler:
 * https://github.com/JetBrains/kotlin/blob/v1.3.50/idea/src/org/jetbrains/kotlin/idea/inspections/RedundantExplicitTypeInspection.kt
 */

/**
 * Local properties do not need their type to be explicitly provided when the inferred type matches the explicit type.
 *
 * <noncompliant>
 * fun function() {
 *     val x: String = "string"
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun function() {
 *     val x = "string"
 * }
 * </compliant>
 */
class RedundantExplicitType(config: Config) :
    Rule(
        config,
        "Type does not need to be stated explicitly and can be removed."
    ),
    RequiresTypeResolution {
    @Suppress("ReturnCount", "ComplexMethod")
    override fun visitProperty(property: KtProperty) {
        if (!property.isLocal) return
        val typeReference = property.typeReference ?: return
        val type = (bindingContext[BindingContext.VARIABLE, property])?.type ?: return
        if (type is AbbreviatedType) return

        when (val initializer = property.initializer) {
            is KtConstantExpression -> if (!initializer.typeIsSameAs(type)) return
            is KtStringTemplateExpression -> if (!KotlinBuiltIns.isString(type)) return
            is KtNameReferenceExpression -> if (typeReference.text != initializer.getReferencedName()) return
            is KtCallExpression -> if (typeReference.text != initializer.calleeExpression?.text) return
            else -> return
        }
        report(CodeSmell(Entity.atName(property), description))
        super.visitProperty(property)
    }

    private fun KtConstantExpression.typeIsSameAs(type: KotlinType) =
        when (node.elementType) {
            KtNodeTypes.BOOLEAN_CONSTANT -> type.isBoolean()
            KtNodeTypes.CHARACTER_CONSTANT -> type.isChar()
            KtNodeTypes.INTEGER_CONSTANT -> {
                if (text.endsWith("L")) {
                    type.isLong()
                } else {
                    type.isInt()
                }
            }
            KtNodeTypes.FLOAT_CONSTANT -> {
                if (text.endsWith("f") || text.endsWith("F")) {
                    type.isFloat()
                } else {
                    type.isDouble()
                }
            }
            else -> false
        }
}
