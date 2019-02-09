package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

/**
 * @author Karol Wrótniak
 */
class CommentInsideBlockSpec : Spek({

    class NotConfiguredEmptyRule : EmptyRule(Config.empty) {
        override fun visitBlockExpression(expression: KtBlockExpression) =
                expression.addFindingIfBlockExprIsEmpty()
    }

    val subject = NotConfiguredEmptyRule()

    it("does not find comment inside an empty block") {
        val findings = subject.lint("{/*comment*/}")
        assertThat(findings).isEmpty()
    }

    it("finds comments inside block") {
        val findings = subject.lint("{}")
        assertThat(findings).hasSize(1)
    }
})
