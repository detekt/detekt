package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isAllowedExceptionName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiverOrThis
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Exceptions should not be swallowed. This rule reports all instances where exceptions are `caught` and not correctly
 * passed (e.g. as a cause) into a newly thrown exception.
 *
 * The exception types configured in `ignoredExceptionTypes` indicate nonexceptional outcomes.
 * These by default configured exception types are part of Java.
 * Therefore, Kotlin developers have to handle them by using the catch clause.
 * For that reason, this rule ignores that these configured exception types are caught.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException(e.message) // e is swallowed
 *     }
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException() // e is swallowed
 *     }
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         bar() // exception is unused
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException(e)
 *     }
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         println(e) // logging is ok here
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class SwallowedException(config: Config) : Rule(
    config,
    "The caught exception is swallowed. The original exception could be lost."
) {

    @Configuration("exception types which should be ignored (both in the catch clause and body)")
    private val ignoredExceptionTypes: List<String> by config(EXCEPTIONS_IGNORED_BY_DEFAULT) { exceptions ->
        exceptions.map { it.removePrefix("*").removeSuffix("*") }
    }

    @Configuration("ignores too generic exception types which match this regex")
    private val allowedExceptionNameRegex: Regex by config("_|(ignore|expected).*", String::toRegex)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        catchClause.catchParameter?.let { catchParameter ->
            val exceptionType = catchParameter.typeReference?.text
            if (!ignoredExceptionTypes.any { exceptionType?.contains(it, ignoreCase = true) == true } &&
                isExceptionSwallowedOrUnused(catchClause) &&
                !catchClause.isAllowedExceptionName(allowedExceptionNameRegex)
            ) {
                report(Finding(Entity.from(catchParameter), description))
            }
        }
        super.visitCatchSection(catchClause)
    }

    private fun isExceptionSwallowedOrUnused(catchClause: KtCatchClause) =
        isExceptionUnused(catchClause) || isExceptionSwallowed(catchClause)

    private fun isExceptionUnused(catchClause: KtCatchClause): Boolean {
        val parameterName = catchClause.catchParameter?.name
        val catchBody = catchClause.catchBody ?: return true
        return !catchBody.anyDescendantOfType<KtNameReferenceExpression> {
            it.text in ignoredExceptionTypes || it.text == parameterName
        }
    }

    private fun isExceptionSwallowed(catchClause: KtCatchClause): Boolean {
        val parameterName = catchClause.catchParameter?.name
        val catchBody = catchClause.catchBody ?: return false
        return catchBody.anyDescendantOfType<KtThrowExpression> { throwExpr ->
            val refs = throwExpr.parameterReferences(parameterName, catchBody)
            refs.isNotEmpty() && refs.all { it is KtDotQualifiedExpression && it.parent !is KtThrowExpression }
        }
    }

    private fun KtThrowExpression.parameterReferences(
        parameterName: String?,
        catchBody: KtExpression,
    ): List<KtExpression> {
        val parameterReferencesInVariables = mutableMapOf<String, KtExpression>()
        return thrownExpression
            ?.collectDescendantsOfType<KtNameReferenceExpression>()
            ?.mapNotNull { reference ->
                val referenceText = reference.text
                if (referenceText == parameterName) {
                    reference.getQualifiedExpressionForReceiverOrThis()
                } else {
                    parameterReferencesInVariables[referenceText]
                        ?: reference.findReferenceInVariable(parameterName, referenceText, catchBody)?.also {
                            parameterReferencesInVariables[referenceText] = it
                        }
                }
            }
            .orEmpty()
    }

    private fun KtExpression.findReferenceInVariable(
        referenceName: String?,
        variableName: String,
        catchBody: KtExpression,
    ): KtExpression? {
        val block = getStrictParentOfType<KtBlockExpression>() ?: return null
        fun find(block: KtBlockExpression): KtExpression? {
            val reference = block
                .findDescendantOfType<KtProperty> { it.name == variableName }
                ?.let { property ->
                    val initializer = property.initializer
                    if (initializer is KtDotQualifiedExpression) {
                        initializer.takeIf { it.receiverExpression.text == referenceName }
                    } else {
                        initializer.takeIf { it?.text == referenceName }
                    }
                }
            return when {
                reference != null -> reference
                block == catchBody -> null
                else -> block.getStrictParentOfType<KtBlockExpression>()?.let { find(it) }
            }
        }
        return find(block)
    }

    companion object {
        @JvmStatic
        val EXCEPTIONS_IGNORED_BY_DEFAULT = listOf(
            "InterruptedException",
            "MalformedURLException",
            "NumberFormatException",
            "ParseException",
        )
    }
}
