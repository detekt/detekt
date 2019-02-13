package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.test.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Marvin Ramin
 */
internal class SuppressibleSpec : Spek({

    describe("a Test rule") {

        it("should not be suppressed by a @Deprecated annotation") {
            assertThat(checkSuppression("Deprecated", "This should no longer be used")).isFalse()
        }

        it("should not be suppressed by a @Suppress annotation for another rule") {
            assertThat(checkSuppression("Suppress", "NotATest")).isFalse()
        }

        it("should not be suppressed by a @SuppressWarnings annotation for another rule") {
            assertThat(checkSuppression("SuppressWarnings", "NotATest")).isFalse()
        }

        it("should be suppressed by a @Suppress annotation for the rule") {
            assertThat(checkSuppression("Suppress", "Test")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation for the rule") {
            assertThat(checkSuppression("SuppressWarnings", "Test")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation for 'all' rules") {
            assertThat(checkSuppression("Suppress", "all")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation for 'ALL' rules") {
            assertThat(checkSuppression("SuppressWarnings", "ALL")).isTrue()
        }

        it("should not be suppressed by a @Suppress annotation with a Checkstyle prefix") {
            assertThat(checkSuppression("Suppress", "Checkstyle:Test")).isFalse()
        }

        it("should not be suppressed by a @SuppressWarnings annotation with a Checkstyle prefix") {
            assertThat(checkSuppression("SuppressWarnings", "Checkstyle:Test")).isFalse()
        }

        it("should be suppressed by a @Suppress annotation with a 'Detekt' prefix") {
            assertThat(checkSuppression("Suppress", "Detekt:Test")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation with a 'Detekt' prefix") {
            assertThat(checkSuppression("SuppressWarnings", "Detekt:Test")).isTrue()
        }

        it("should be suppressed by a @Suppress annotation with a 'detekt' prefix") {
            assertThat(checkSuppression("Suppress", "detekt:Test")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix") {
            assertThat(checkSuppression("SuppressWarnings", "detekt:Test")).isTrue()
        }

        it("should be suppressed by a @Suppress annotation with a 'detekt' prefix with a dot") {
            assertThat(checkSuppression("Suppress", "detekt.Test")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix with a dot") {
            assertThat(checkSuppression("SuppressWarnings", "detekt.Test")).isTrue()
        }

        it("should not be suppressed by a @Suppress annotation with a 'detekt' prefix with a wrong separator") {
            assertThat(checkSuppression("Suppress", "detekt/Test")).isFalse()
        }

        it("should not be suppressed by a @SuppressWarnings annotation with a 'detekt' prefix with a wrong separator") {
            assertThat(checkSuppression("SuppressWarnings", "detekt/Test")).isFalse()
        }

        it("should be suppressed by a @Suppress annotation with an alias") {
            assertThat(checkSuppression("Suppress", "alias")).isTrue()
        }

        it("should be suppressed by a @SuppressWarnings annotation with an alias") {
            assertThat(checkSuppression("SuppressWarnings", "alias")).isTrue()
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
