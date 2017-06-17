package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * @author Artur Bosch
 */
class EmptyClassBlock(config: Config) : EmptyRule("EmptyClassBlock", config = config) {

	override fun visitClassOrObject(context: Context, classOrObject: KtClassOrObject) {
		classOrObject.getBody()?.declarations?.let {
			if (it.isEmpty()) context.report(CodeSmell(ISSUE, Entity.from(classOrObject)))
		}
	}

	companion object {
		val ISSUE = Issue("EmptyClassBlock", Issue.Severity.Minor)
	}
}
