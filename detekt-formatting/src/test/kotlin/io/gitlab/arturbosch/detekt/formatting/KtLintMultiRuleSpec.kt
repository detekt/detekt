package io.gitlab.arturbosch.detekt.formatting

import com.pinterest.ktlint.core.Rule.Modifier.Last
import com.pinterest.ktlint.core.Rule.Modifier.RestrictToRoot
import com.pinterest.ktlint.core.Rule.Modifier.RestrictToRootLast
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class KtLintMultiRuleSpec : Spek({

    describe("KtLintMultiRule rule") {

        it("sorts rules correctly") {
            val ktlintRule = KtLintMultiRule(Config.empty)
            ktlintRule.visitFile(compileContentForTest(""))
            val sortedRules = ktlintRule.getSortedRules()
            assertThat(sortedRules).isNotEmpty
            assertThat(sortedRules.indexOfFirst { it.wrapping is RestrictToRoot })
                .isGreaterThan(-1)
                .isLessThan(sortedRules.indexOfFirst { it.wrapping !is RestrictToRoot })
            assertThat(sortedRules.indexOfFirst { it.wrapping !is RestrictToRoot })
                .isGreaterThan(-1)
                .isLessThan(sortedRules.indexOfFirst { it.wrapping is RestrictToRootLast })
            assertThat(sortedRules.indexOfFirst { it.wrapping is RestrictToRootLast })
                .isGreaterThan(-1)
                .isLessThan(sortedRules.indexOfFirst { it.wrapping is Last })
        }
    }
})
