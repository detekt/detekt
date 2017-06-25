package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.allChildren

class LateinitUsage(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "Usage of lateinit")

	override fun visitProperty(property: KtProperty) {
		val lateinitModifiers = property.modifierList
				?.allChildren
				?.filter { it.text == "lateinit" }
				?.count() ?: 0

		if (lateinitModifiers > 0) {
			report(CodeSmell(issue, Entity.from(property)))
		}
	}
}