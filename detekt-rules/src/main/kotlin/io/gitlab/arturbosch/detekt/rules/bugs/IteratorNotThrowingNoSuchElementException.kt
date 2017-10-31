package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.bugs.iterator.getMethod
import io.gitlab.arturbosch.detekt.rules.bugs.iterator.isImplementingIterator
import io.gitlab.arturbosch.detekt.rules.bugs.iterator.throwsNoSuchElementExceptionThrown
import org.jetbrains.kotlin.psi.KtClassOrObject

class IteratorNotThrowingNoSuchElementException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("IteratorNotThrowingNoSuchElementException", Severity.Defect,
			"The next() method of an Iterator implementation should throw a NoSuchElementException " +
					"when there are no more elements to return",
			Debt.TEN_MINS)

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (classOrObject.isImplementingIterator()) {
			val nextMethod = classOrObject.getMethod("next")
			if (nextMethod != null && !nextMethod.throwsNoSuchElementExceptionThrown()) {
				report(CodeSmell(issue, Entity.from(classOrObject)))
			}
		}
		super.visitClassOrObject(classOrObject)
	}
}
