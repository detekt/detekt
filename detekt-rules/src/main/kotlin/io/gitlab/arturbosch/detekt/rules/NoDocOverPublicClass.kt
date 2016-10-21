package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClass(config: Config = Config.EMPTY) : Rule("NoDocOverPublicClass", Severity.Maintainability, config) {

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (classOrObject.isPublicNotOverriden()) {
			addFindings(CodeSmell(id, Location.from(classOrObject, classOrObject.getBody())))
		}
		super.visitClassOrObject(classOrObject)
	}

}