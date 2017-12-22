package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports empty classes. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class EmptyClassBlock(config: Config) : EmptyRule(config) {

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		classOrObject.getBody()?.declarations?.let {
			if (it.isEmpty()) report(CodeSmell(issue, Entity.from(classOrObject), "The class or object " +
					" ${classOrObject.name} is empty."))
		}
	}

}
