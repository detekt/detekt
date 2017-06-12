package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.complexity.NestedBlockDepth
import io.gitlab.arturbosch.detekt.test.RuleTest
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class NestedBlockDepthSpec : SubjectSpek<NestedBlockDepth>({
	subject { NestedBlockDepth(threshold = 3) }
	itBehavesLike(CommonSpec())

	describe("nested classes are also considered") {
		it("should detect only the nested large class") {
            val findings = subject.lint(Case.NestedClasses.path())
            assertThat(findings).hasSize(1)
            assertThat((findings[0] as ThresholdedCodeSmell).value).isEqualTo(5)
		}
	}

})

class NestedBlockDepthTest : RuleTest {

	override val rule: Rule = NestedBlockDepth()

	@Test
	fun elseIfDoesNotCountAsTwo() {
		val actual = """
	override fun procedure(node: ASTNode) {
		val psi = node.psi
		if (psi.isNotPartOfEnum() && psi.isNotPartOfString()) {
			if (psi.isDoubleSemicolon()) {
				context.report(CodeSmell(id, Entity.from(psi)))
				withAutoCorrect {
					deleteOneOrTwoSemicolons(node as LeafPsiElement)
				}
			} else if (psi.isSemicolon()) {
				val nextLeaf = psi.nextLeaf()
				if (nextLeaf.isSemicolonOrEOF() || nextTokenHasSpaces(nextLeaf)) {
					context.report(CodeSmell(id, Entity.from(psi)))
					withAutoCorrect { psi.delete() }
				}
			}
		}
	}
"""
		assertThat(rule.lint(actual)).isEmpty()
	}

}