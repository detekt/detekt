package dev.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaContextParameterApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.allSupertypes
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.symbol
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtParameter

/**
 * Custom exception classes that cannot be recreated by the coroutine stack trace recovery mechanism are reported.
 *
 * When an exception crosses a coroutine boundary, kotlinx.coroutines can recover the original stack trace by
 * creating a copy of the exception instance. This works automatically for exceptions that have a constructor taking
 * only a message, a cause, both, or no arguments at all. Exceptions that cannot be constructed this way must
 * implement `kotlin.coroutines.debug.StackTraceRecoverable` (introduced in Kotlin 2.4.20) and override
 * `copyForStackTraceRecovery()` to return a copy of themselves, otherwise the original stack trace is lost.
 *
 * This rule reports exception classes that provide no standard constructor and do not implement
 * `kotlin.coroutines.debug.StackTraceRecoverable`.
 *
 * See [Support for coroutine stack trace recovery](https://kotlinlang.org/docs/whatsnew2420.html#support-for-coroutine-stack-trace-recovery)
 * for more information.
 *
 * <noncompliant>
 * class NetworkException(val errorCode: Int) : RuntimeException()
 * </noncompliant>
 *
 * <compliant>
 * class NetworkException(val errorCode: Int) : RuntimeException(),
 *     StackTraceRecoverable<NetworkException> {
 *     override fun copyForStackTraceRecovery(): NetworkException = NetworkException(errorCode)
 * }
 *
 * // A constructor taking only a message, a cause, both or no arguments makes the recovery work automatically
 * class TimeoutException(message: String, cause: Throwable?) : RuntimeException(message, cause)
 * </compliant>
 */
class MissingStackTraceRecoverable(config: Config) :
    Rule(
        config,
        "Exceptions that cannot be recreated by the coroutine stack trace recovery mechanism lose their original " +
            "stack trace when rethrown from another coroutine. Provide a constructor taking only a message, a " +
            "cause, both or no arguments, or implement `kotlin.coroutines.debug.StackTraceRecoverable`.",
    ),
    RequiresAnalysisApi {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        val ktClass = classOrObject as? KtClass ?: return
        if (ktClass.isInterface()) return
        checkClass(ktClass)
    }

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun KaType.isThrowableType(): Boolean = symbol?.classId in THROWABLE_CLASS_IDS

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun KaType.isStackTraceRecoverableType(): Boolean = symbol?.classId == STACK_TRACE_RECOVERABLE_CLASS_ID

    private fun checkClass(ktClass: KtClass) {
        analyze(ktClass) {
            val classSymbol = ktClass.symbol as? KaClassSymbol ?: return@analyze

            val directSuperTypes = classSymbol.superTypes
            val transitiveSuperTypes = directSuperTypes + directSuperTypes.flatMap { it.allSupertypes }
            if (transitiveSuperTypes.any { it.isStackTraceRecoverableType() }) return@analyze
            if (transitiveSuperTypes.none { it.isThrowableType() }) return@analyze

            val constructorParamClassIds = ktClass.constructorParameterLists().map { ctorParams ->
                ctorParams.map { param -> param.typeReference?.type?.symbol?.classId }
            }
            if (constructorParamClassIds.isEmpty()) return@analyze
            if (constructorParamClassIds.any { it.isStandardConstructorShape() }) return@analyze

            report(
                Finding(
                    Entity.atName(ktClass),
                    "The exception `${ktClass.name}` cannot be recovered by the coroutine stack trace recovery " +
                        "mechanism. Add a constructor taking only a message, a cause, both or no arguments, or " +
                        "implement `kotlin.coroutines.debug.StackTraceRecoverable`.",
                )
            )
        }
    }

    private fun KtClass.constructorParameterLists(): List<List<KtParameter>> =
        buildList {
            primaryConstructor?.let { add(it.valueParameters) }
            secondaryConstructors.forEach { add(it.valueParameters) }
        }

    private fun List<ClassId?>.isStandardConstructorShape(): Boolean =
        when (size) {
            0 -> true
            1 -> this[0] in STRING_OR_THROWABLE_CLASS_IDS
            2 -> this[0] in STRING_CLASS_IDS && this[1] in THROWABLE_CLASS_IDS
            else -> false
        }

    private companion object {
        val THROWABLE_CLASS_IDS = setOf(
            ClassId.topLevel(FqName("java.lang.Throwable")),
            ClassId.topLevel(FqName("kotlin.Throwable")),
        )
        val STRING_CLASS_IDS = setOf(
            ClassId.topLevel(FqName("java.lang.String")),
            ClassId.topLevel(FqName("kotlin.String")),
        )
        val STRING_OR_THROWABLE_CLASS_IDS = STRING_CLASS_IDS + THROWABLE_CLASS_IDS
        val STACK_TRACE_RECOVERABLE_CLASS_ID =
            ClassId.topLevel(FqName("kotlin.coroutines.debug.StackTraceRecoverable"))
    }
}
