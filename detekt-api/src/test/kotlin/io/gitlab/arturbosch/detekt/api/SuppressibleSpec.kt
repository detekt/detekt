package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * @author Marvin Ramin
 */
internal class SuppressibleSpec : Spek({

	given("a Test rule") {

		it("should not be suppressed by a @Deprecated annotation") {
			assertFalse { checkSuppression("Deprecated", "This should no longer be used") }
		}

		it("should not be suppressed by a @Suppress annotation for another rule") {
			assertFalse { checkSuppression("Suppress", "NotATest") }
		}

		it("should not be suppressed by a @SuppressWarnings annotation for another rule") {
			assertFalse { checkSuppression("SuppressWarnings", "NotATest") }
		}

		it("should be suppressed by a @Suppress annotation for the rule") {
			assertTrue { checkSuppression("Suppress", "Test") }
		}

		it("should be suppressed by a @SuppressWarnings annotation for the rule") {
			assertTrue { checkSuppression("SuppressWarnings", "Test") }
		}

		it("should be suppressed by a @SuppressWarnings annotation for 'all' rules") {
			assertTrue { checkSuppression("Suppress", "all") }
		}

		it("should be suppressed by a @SuppressWarnings annotation for 'ALL' rules") {
			assertTrue { checkSuppression("SuppressWarnings", "ALL") }
		}

		it("should not be suppressed by a @Suppress annotation with a Checkstyle prefix") {
			assertFalse { checkSuppression("Suppress", "Checkstyle:Test") }
		}

		it("should not be suppressed by a @SuppressWarnings annotation with a Checkstyle prefix") {
			assertFalse { checkSuppression("SuppressWarnings", "Checkstyle:Test") }
		}

		it("should be suppressed by a @Suppress annotation with a 'Detekt' prefix") {
			assertTrue { checkSuppression("Suppress", "Detekt:Test") }
		}

		it("should be suppressed by a @SuppressWarnings annotation with a 'Detekt' prefix") {
			assertTrue { checkSuppression("SuppressWarnings", "Detekt:Test") }
		}

		it("should be suppressed by a @Suppress annotation with a 'detekt' prefix") {
			assertTrue { checkSuppression("Suppress", "detekt:Test") }
		}

		it("should be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix") {
			assertTrue { checkSuppression("SuppressWarnings", "detekt:Test") }
		}

		it("should be suppressed by a @Suppress annotation with a 'detekt' prefix with a dot") {
			assertTrue { checkSuppression("Suppress", "detekt.Test") }
		}

		it("should be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix with a dot") {
			assertTrue { checkSuppression("SuppressWarnings", "detekt.Test") }
		}

		it("should not be suppressed by a @Suppress annotation with a 'detekt' prefix with a wrong separator") {
			assertFalse { checkSuppression("Suppress", "detekt/Test") }
		}

		it("should not be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix with a wrong separator") {
			assertFalse { checkSuppression("SuppressWarnings", "detekt/Test") }
		}

		it("should be suppressed by a @Suppress annotation with an alias") {
			assertTrue { checkSuppression("Suppress", "alias") }
		}

		it("should be suppressed by a @SuppressWarnings annotation with an alias") {
			assertTrue { checkSuppression("SuppressWarnings", "alias") }
		}
	}
})

private fun checkSuppression(annotation: String, argument: String): Boolean {
	val annotated = """
			@$annotation("$argument")
			class Test {}
			 """
	val file = compileContentForTest(annotated)
	val annotatedClass = file.children.first { it is KtClass } as KtAnnotated
	return annotatedClass.isSuppressedBy("Test", setOf("alias"))
}
