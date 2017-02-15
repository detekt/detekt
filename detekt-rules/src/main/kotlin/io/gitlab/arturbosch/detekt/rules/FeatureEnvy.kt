package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmellRule
import io.gitlab.arturbosch.detekt.api.Config
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class FeatureEnvy(config: Config = Config.empty) : CodeSmellRule("FeatureEnvy", config) {

	override fun visitClassBody(classBody: KtClassBody) {
		val properties = classBody.properties
		val functions = classBody.declarations
				.filterIsInstance(KtNamedFunction::class.java)



		super.visitClassBody(classBody)
	}

}