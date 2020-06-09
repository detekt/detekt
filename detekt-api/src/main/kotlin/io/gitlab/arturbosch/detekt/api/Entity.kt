package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.buildFullSignature
import io.gitlab.arturbosch.detekt.api.internal.searchClass
import io.gitlab.arturbosch.detekt.api.internal.searchName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * Stores information about a specific code fragment.
 */
data class Entity(
    @Deprecated("Will be made private in the future. Use queries on 'ktElement' instead.")
    val name: String,
    @Deprecated("Will be removed in the future. Use queries on 'ktElement' instead.")
    val className: String,
    val signature: String,
    val location: Location,
    val ktElement: KtElement? = null
) : Compactable {

    @Suppress("DEPRECATION")
    override fun compact(): String = "[$name] at ${location.compact()}"

    companion object {
        /**
         * Factory function which retrieves all needed information from the PsiElement itself.
         */
        fun from(element: PsiElement, offset: Int = 0): Entity {
            val location = Location.from(element, offset)
            return from(element, location)
        }

        /**
         * Create an entity at the location of the identifier of given named declaration.
         */
        fun atName(element: KtNamedDeclaration): Entity =
            from(element.nameIdentifier ?: element, element)

        /**
         * Create an entity at the location of the package, first import or first declaration.
         */
        fun atPackageOrFirstDecl(file: KtFile): Entity =
            from(file.packageDirective ?: file.firstChild ?: file, file)

        /**
         * Use this factory method if the location can be calculated much more precisely than
         * using the given PsiElement.
         */
        fun from(element: PsiElement, location: Location): Entity = from(element, element, location)

        private fun from(elementToReport: PsiElement, elementForSignature: PsiElement): Entity =
            from(elementToReport, elementForSignature, Location.from(elementToReport))

        private fun from(
            elementToReport: PsiElement,
            elementForSignature: PsiElement,
            location: Location
        ): Entity {
            val name = elementToReport.searchName()
            val signature = elementForSignature.buildFullSignature()
            val clazz = elementToReport.searchClass()
            val ktElement = elementToReport.getNonStrictParentOfType<KtElement>() ?: error("KtElement expected")
            return Entity(name, clazz, signature, location, ktElement)
        }
    }
}
