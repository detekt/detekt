package dev.detekt.rules.comments

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isPublicNotOverridden
import org.jetbrains.kotlin.analysis.api.KaContextParameterApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.isMarkedNullable
import org.jetbrains.kotlin.analysis.api.components.isNothingType
import org.jetbrains.kotlin.analysis.api.components.isUnitType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.isObjectLiteral
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * This rule reports documented public functions that do not document their return value.
 *
 * There are three cases to distinguish:
 * - Functions returning `Unit` do not need a `@return` tag.
 * - Functions returning `Nothing` must document why they never return using a `@throws` or `@exception` tag.
 * - Every other function must document its return value using a `@return` tag.
 *
 * The return type is resolved by the analysis API, so inferred return types are checked as well.
 * Functions without any KDoc are not reported by this rule, use `UndocumentedPublicFunction` for that.
 *
 * <noncompliant>
 * /**
 *  * Returns the current count.
 *  */
 * fun count(): Int = 42
 *
 * /**
 *  * Always fails.
 *  */
 * fun fail(): Nothing = throw IllegalStateException()
 * </noncompliant>
 *
 * <compliant>
 * /**
 *  * Returns the current count.
 *  *
 *  * @return The current count.
 *  */
 * fun count(): Int = 42
 *
 * /**
 *  * Always fails.
 *  *
 *  * @throws IllegalStateException Always.
 *  */
 * fun fail(): Nothing = throw IllegalStateException()
 *
 * /**
 *  * Prints the current count.
 *  */
 * fun printCount() {
 *     println(42)
 * }
 * </compliant>
 */
class UndocumentedPublicFunctionReturn(config: Config) :
    Rule(config, "Public functions should document their return value."),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        val doc = function.docComment ?: return
        if (!function.shouldBeDocumented()) return

        val missingTagMessage = analyze(function) { function.returnType.getMissingTagMessageOrNull() } ?: return
        if (doc.isTaggedWithAny(missingTagMessage.requiredTags)) return

        report(
            Finding(
                Entity.atName(function),
                missingTagMessage.message,
            )
        )
    }

    private fun KtNamedFunction.shouldBeDocumented(): Boolean =
        !isLocal && isPublicNotOverridden() &&
            parents.filterIsInstance<KtClassOrObject>().all {
                it.isPublic && !it.isLocal && !it.isObjectLiteral()
            }

    @OptIn(KaContextParameterApi::class)
    context(_: KaSession)
    private fun KaType.getMissingTagMessageOrNull(): MissingTagMessage? =
        when {
            isUnitType && !isMarkedNullable -> null

            isNothingType && !isMarkedNullable -> MissingTagMessage(
                requiredTags = listOf("throws", "exception"),
                message = "The function returns `Nothing` but does not document a `@throws` or " +
                    "`@exception` tag.",
            )

            else -> MissingTagMessage(
                requiredTags = listOf("return"),
                message = "The function return value is not documented with a `@return` tag.",
            )
        }

    private fun KDoc.isTaggedWithAny(tagNames: List<String>): Boolean =
        getAllSections().any { section ->
            tagNames.any { section.findTagsByName(it).isNotEmpty() }
        }

    private data class MissingTagMessage(val requiredTags: List<String>, val message: String)
}
