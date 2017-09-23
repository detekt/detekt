package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.providers.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.lint
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek

/**
 * @author Artur Bosch
 */
class EmptyBlocksMultiRuleTest : SubjectSpek<EmptyBlocks>({

	subject { EmptyBlocks() }

	val file = compileForTest(Case.Empty.path())

	describe("multi rule with all empty block rules") {

		it("should report one finding per rule") {
			val findings = subject.lint(file)
			// -1 because the empty kt file rule doesn't get triggered in the 'Empty' test file
			val rulesSize = subject.rules.size - 1
			assertThat(findings).hasSize(rulesSize)
		}

		it("should not report any as all empty block rules are deactivated") {
			val config = YamlConfig.loadResource(resource("deactivated-empty-blocks.yml").toURL())
			val ruleSet = EmptyCodeProvider().buildRuleset(config)

			val findings = ruleSet?.accept(file)

			assertThat(findings).hasSize(0)
		}

		it("reports an empty kt file") {
			val findings = subject.lint(Case.EmptyKtFile.path())
			assertThat(findings).hasSize(1)
		}
	}
})
