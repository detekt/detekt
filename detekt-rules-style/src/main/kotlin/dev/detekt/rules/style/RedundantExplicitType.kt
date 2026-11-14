package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.session.analyze
import org.jetbrains.kotlin.analysis.api.types.KaStandardTypeClassIds
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.classId
import org.jetbrains.kotlin.analysis.api.types.type
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

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
    RequiresAnalysisApi {

    @Suppress("ReturnCount", "ComplexMethod")
    override fun visitProperty(property: KtProperty) {
        if (!property.isLocal) return
        val typeReference = property.typeReference ?: return
        analyze(typeReference) {
            val type = typeReference.type
            if (type.abbreviation != null) return

            when (val initializer = property.initializer) {
                is KtConstantExpression -> if (!typeIsSameAs(initializer, type)) return
                is KtStringTemplateExpression -> if (type.classId != KaStandardTypeClassIds.STRING) return
                is KtNameReferenceExpression -> if (typeReference.text != initializer.getReferencedName()) return
                is KtCallExpression -> if (typeReference.text != initializer.calleeExpression?.text) return
                else -> return
            }
        }
        report(Finding(Entity.atName(property), description))
        super.visitProperty(property)
    }

    context(_: KaSession)
    private fun typeIsSameAs(expression: KtConstantExpression, type: KaType) =
        when (expression.node.elementType) {
            KtNodeTypes.BOOLEAN_CONSTANT -> type.classId == KaStandardTypeClassIds.BOOLEAN

            KtNodeTypes.CHARACTER_CONSTANT -> type.classId == KaStandardTypeClassIds.CHAR

            KtNodeTypes.INTEGER_CONSTANT -> {
                if (expression.text.endsWith("L")) {
                    type.classId == KaStandardTypeClassIds.LONG
                } else {
                    type.classId == KaStandardTypeClassIds.INT
                }
            }

            KtNodeTypes.FLOAT_CONSTANT -> {
                if (expression.text.endsWith("f") || expression.text.endsWith("F")) {
                    type.classId == KaStandardTypeClassIds.FLOAT
                } else {
                    type.classId == KaStandardTypeClassIds.DOUBLE
                }
            }

            else -> false
        }
}
