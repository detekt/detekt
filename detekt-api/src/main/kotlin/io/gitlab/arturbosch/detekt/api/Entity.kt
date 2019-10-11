package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.buildFullSignature
import io.gitlab.arturbosch.detekt.api.internal.searchClass
import io.gitlab.arturbosch.detekt.api.internal.searchName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * Stores information about a specific code fragment.
 */
data class Entity(
    val name: String,
    val className: String,
    val signature: String,
    val location: Location,
    val ktElement: KtElement? = null
) : Compactable {

    override fun compact(): String = "[$name] at ${location.compact()}"

    companion object {
        /**
         * Factory function which retrieves all needed information from the PsiElement itself.
         */
        fun from(element: PsiElement, offset: Int = 0): Entity {
            val name = element.searchName()
            val signature = element.buildFullSignature()
            val clazz = element.searchClass()
            val ktElement = element.getNonStrictParentOfType<KtElement>()!!
            return Entity(name, clazz, signature, Location.from(element, offset), ktElement)
        }
    }
}
