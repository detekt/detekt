package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * @author Artur Bosch
 */
class EmptyClassBlock(config: Config) : EmptyRule("EmptyClassBlock", config = config) {

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		classOrObject.getBody()?.declarations?.let {
			if (it.isEmpty()) addFindings(CodeSmell(id, severity, Entity.from(classOrObject)))
		}
	}

}
