package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.successfulVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
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
            val allProperties = classMembers.filterIsInstance<KtProperty>().mapNotNull {
                val name = it.name ?: return@mapNotNull null
                val callableId = it.symbol.callableId ?: return@mapNotNull null
                name to callableId
            }.toMap()

            val declaredProperties = mutableSetOf<CallableId>()

            classMembers.forEach { member ->
                member.forEachDescendantOfType<KtNameReferenceExpression> {
                    val property = allProperties[it.text]
                    if (property != null &&
                        property !in declaredProperties &&
                        property == it.resolveToCall()?.successfulVariableAccessCall()?.symbol?.callableId
                    ) {
                        report(Finding(Entity.from(it), "'${it.text}' is used before declaration."))
                    }
                }
                if (member is KtProperty) {
                    declaredProperties.addIfNotNull(allProperties[member.name])
                }
            }
        }
    }
}
