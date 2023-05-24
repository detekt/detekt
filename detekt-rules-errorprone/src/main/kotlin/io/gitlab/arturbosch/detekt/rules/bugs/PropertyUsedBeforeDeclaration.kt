package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.utils.addIfNotNull

/**
 * Reports properties that are used before declaration.
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
@RequiresTypeResolution
class PropertyUsedBeforeDeclaration(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Properties before declaration should not be used.",
        Debt.FIVE_MINS
    )

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)

        val classMembers = classOrObject.body?.children ?: return

        val allProperties = classMembers.filterIsInstance<KtProperty>().mapNotNull {
            val name = it.name ?: return@mapNotNull null
            val descriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, it] ?: return@mapNotNull null
            name to descriptor
        }.toMap()

        val declaredProperties = mutableSetOf<DeclarationDescriptor>()

        classMembers.forEach { member ->
            member.forEachDescendantOfType<KtNameReferenceExpression> {
                val property = allProperties[it.text]
                if (property != null && property !in declaredProperties && property == it.descriptor()) {
                    report(CodeSmell(issue, Entity.from(it), "'${it.text}' is used before declaration."))
                }
            }
            if (member is KtProperty) {
                declaredProperties.addIfNotNull(allProperties[member.name])
            }
        }
    }

    private fun KtNameReferenceExpression.descriptor() = getResolvedCall(bindingContext)?.resultingDescriptor
}
