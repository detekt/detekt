package dev.detekt.rules.potentialbugs

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.successfulVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.utils.addIfNotNull

/**
 * Reports properties that are used before declaration, including properties accessed through private
 * functions during construction.
 *
 * <noncompliant>
 * class C {
 *     private val number
 *         get() = if (isValid) 1 else 0
 *
 *     val list = listOf(number)
 *
 *     private val isValid = true
 * }
 *
 * fun main() {
 *     println(C().list) // [0]
 * }
 * </noncompliant>
 *
 * <compliant>
 * class C {
 *     private val isValid = true
 *
 *     private val number
 *         get() = if (isValid) 1 else 0
 *
 *     val list = listOf(number)
 * }
 *
 * fun main() {
 *     println(C().list) // [1]
 * }
 * </compliant>
 */
class PropertyUsedBeforeDeclaration(config: Config) :
    Rule(
        config,
        "Properties before declaration should not be used."
    ),
    RequiresAnalysisApi {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)

        val classMembers = classOrObject.body?.children?.filterNot { it is KtClassOrObject } ?: return

        analyze(classOrObject) {
            val reporter = PropertyUsageReporter(classOrObject, classMembers.propertyCallableIds(), ::report)
            classMembers.forEach { reporter.visitMember(it) }
        }
    }
}

private class PropertyUsageReporter(
    private val classOrObject: KtClassOrObject,
    private val allProperties: Map<String, CallableId>,
    private val report: (Finding) -> Unit,
) {
    private val declaredProperties = mutableSetOf<CallableId>()
    private val reportedReferences = mutableSetOf<KtNameReferenceExpression>()

    context(session: KaSession)
    fun visitMember(member: PsiElement) {
        member.forEachDescendantOfType<KtNameReferenceExpression> {
            reportIfUsedBeforeDeclaration(it)
        }
        member.constructionInitializer()?.let {
            reportCallsAccessingUndeclaredProperties(it)
        }
        declareIfProperty(member)
    }

    private fun declareIfProperty(member: PsiElement) {
        if (member is KtProperty) {
            declaredProperties.addIfNotNull(allProperties[member.name])
        }
    }

    context(session: KaSession)
    private fun reportIfUsedBeforeDeclaration(reference: KtNameReferenceExpression) {
        val property = allProperties[reference.text] ?: return
        if (property in declaredProperties) return
        val resolvedProperty = with(session) {
            reference.resolveToCall()?.successfulVariableAccessCall()?.symbol?.callableId
        }
        if (property != resolvedProperty) return

        if (reportedReferences.add(reference)) {
            report(Finding(Entity.from(reference), "'${reference.text}' is used before declaration."))
        }
    }

    context(session: KaSession)
    private fun reportCallsAccessingUndeclaredProperties(root: KtElement) {
        val visitedFunctions = mutableSetOf<KtNamedFunction>()

        fun visitFunction(function: KtNamedFunction) {
            if (!visitedFunctions.add(function)) return

            function.forEachDescendantIgnoringNestedDeclarations(
                onReference = { reportIfUsedBeforeDeclaration(it) },
                onCall = { call -> call.resolveToPrivateClassFunction()?.let(::visitFunction) },
            )
        }

        root.forEachDescendantIgnoringNestedDeclarations(
            onReference = {},
            onCall = { call -> call.resolveToPrivateClassFunction()?.let(::visitFunction) },
        )
    }

    context(session: KaSession)
    private fun KtCallExpression.resolveToPrivateClassFunction(): KtNamedFunction? =
        (with(session) { resolveToCall()?.singleFunctionCallOrNull()?.symbol?.psi } as? KtNamedFunction)
            ?.takeIf { it.parent == classOrObject.body && it.hasModifier(KtTokens.PRIVATE_KEYWORD) }
}

context(session: KaSession)
private fun List<PsiElement>.propertyCallableIds(): Map<String, CallableId> =
    filterIsInstance<KtProperty>().mapNotNull {
        val name = it.name ?: return@mapNotNull null
        val callableId = with(session) { it.symbol.callableId } ?: return@mapNotNull null
        name to callableId
    }.toMap()

private fun PsiElement.constructionInitializer(): KtElement? =
    when (this) {
        is KtClassInitializer -> body
        is KtProperty -> initializer
        else -> null
    }

private fun KtElement.forEachDescendantIgnoringNestedDeclarations(
    onReference: (KtNameReferenceExpression) -> Unit,
    onCall: (KtCallExpression) -> Unit,
) {
    val root = this
    accept(
        object : KtTreeVisitorVoid() {
            override fun visitNamedFunction(function: KtNamedFunction) {
                if (function == root) {
                    super.visitNamedFunction(function)
                }
            }

            override fun visitClassOrObject(classOrObject: KtClassOrObject) {
                if (classOrObject == root) {
                    super.visitClassOrObject(classOrObject)
                }
            }

            override fun visitCallExpression(expression: KtCallExpression) {
                onCall(expression)
                super.visitCallExpression(expression)
            }

            override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
                if (expression is KtNameReferenceExpression) {
                    onReference(expression)
                }
                super.visitSimpleNameExpression(expression)
            }
        }
    )
}
