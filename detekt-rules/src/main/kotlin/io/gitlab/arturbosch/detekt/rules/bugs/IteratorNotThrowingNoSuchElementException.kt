package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.bugs.util.getMethod
import io.gitlab.arturbosch.detekt.rules.bugs.util.isImplementingIterator
import io.gitlab.arturbosch.detekt.rules.bugs.util.throwsNoSuchElementExceptionThrown
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Reports implementations of the Iterator interface which do not throw a NoSuchElementException in the
 * implementation of the next() method. When there are no more elements to return an Iterator should throw a
 * NoSuchElementException.
 *
 * See: https://docs.oracle.com/javase/7/docs/api/java/util/Iterator.html#next()
 *
 * <noncompliant>
 * class MyIterator : Iterator<String> {
 *
 *     public Integer next() {
 *         if (!this.hasNext()) {
 *             throw NoSuchElementException()
 *         }
 *     }
 * }
 * </noncompliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class IteratorNotThrowingNoSuchElementException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("IteratorNotThrowingNoSuchElementException", Severity.Defect,
			"The next() method of an Iterator implementation should throw a NoSuchElementException " +
					"when there are no more elements to return",
			Debt.TEN_MINS)

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (classOrObject.isImplementingIterator()) {
			val nextMethod = classOrObject.getMethod("next")
			if (nextMethod != null && !nextMethod.throwsNoSuchElementExceptionThrown()) {
				report(CodeSmell(issue, Entity.from(classOrObject), message = ""))
			}
		}
		super.visitClassOrObject(classOrObject)
	}
}
