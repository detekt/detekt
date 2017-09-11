package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.CommonSpec
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike
import kotlin.test.assertEquals

/**
 * @author Artur Bosch
 */
class NestedBlockDepthSpec : SubjectSpek<NestedBlockDepth>({
	subject { NestedBlockDepth(threshold = 3) }
	itBehavesLike(CommonSpec())

	describe("nested classes are also considered") {
		it("should detect only the nested large class") {
			subject.lint(Case.NestedClasses.path())
			assertEquals(subject.findings.size, 1)
			assertEquals((subject.findings[0] as ThresholdedCodeSmell).value, 5)
		}

		it("should not count else if as two") {
			subject.lint(nestedBlockCode)
			assertThat(subject.findings).isEmpty()
		}
	}

})

val nestedBlockCode = 	"""
	override fun procedure(node: ASTNode) {
		val psi = node.psi
		if (psi.isNotPartOfEnum() && psi.isNotPartOfString()) {
			if (psi.isDoubleSemicolon()) {
				addFindings(CodeSmell(id, Entity.from(psi)))
				withAutoCorrect {
					deleteOneOrTwoSemicolons(node as LeafPsiElement)
				}
			} else if (psi.isSemicolon()) {
				val nextLeaf = psi.nextLeaf()
				if (nextLeaf.isSemicolonOrEOF() || nextTokenHasSpaces(nextLeaf)) {
					addFindings(CodeSmell(id, Entity.from(psi)))
					withAutoCorrect { psi.delete() }
				}
			}
		}
	}"""
