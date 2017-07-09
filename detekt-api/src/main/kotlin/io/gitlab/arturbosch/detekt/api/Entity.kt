package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement

/**
 * Stores information about a specific code fragment.
 *
 * @author Artur Bosch
 */
data class Entity(val name: String,
				  val className: String,
				  val signature: String,
				  val location: Location,
				  val ktElement: KtElement? = null) : Compactable {

	override fun compact() = "[$name] at ${location.compact()}"

	companion object {
		fun from(element: PsiElement, offset: Int = 0): Entity {
			val name = element.searchName()
			val signature = element.buildFullSignature()
			val clazz = element.searchClass()
			return Entity(name, clazz, signature, Location.from(element, offset),
					if (element is KtElement) element else null)
		}
	}
}
