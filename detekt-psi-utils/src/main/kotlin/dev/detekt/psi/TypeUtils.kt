package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaFirDiagnostic
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils

fun KotlinType.fqNameOrNull(): FqName? = TypeUtils.getClassDescriptor(this)?.fqNameOrNull()

/**
 * Return if overall expression is nullable or not nullable
 *
 * ```kotlin
 * var a: Int? = null
 * val b = a // RHS expression will be nullable
 * val c = a!! // RHS expression will be not nullable
 * val d = (a ?: error("null assertion message")) // RHS expression will be not nullable
 * val c = 1?.and(2) // RHS expression will be not nullable
 * ```
 * [shouldConsiderPlatformTypeAsNullable] determines the behaviour of platform type. Passing true considers
 * the platform type as nullable otherwise it is not considered nullable in case of false
 */
@OptIn(KaExperimentalApi::class)
fun KtExpression.isNullable(shouldConsiderPlatformTypeAsNullable: Boolean): Boolean {
    if (this is KtSafeQualifiedExpression) {
        analyze(this) {
            return diagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
                .none { it is KaFirDiagnostic.UnnecessarySafeCall }
        }
    }
    analyze(this) {
        return if (expressionType?.hasFlexibleNullability == true && !shouldConsiderPlatformTypeAsNullable) {
            false
        } else {
            expressionType?.canBeNull == true
        }
    }
}
