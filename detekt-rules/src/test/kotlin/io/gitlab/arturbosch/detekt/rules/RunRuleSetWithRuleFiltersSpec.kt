package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.rules.empty.EmptyBlocks
import io.gitlab.arturbosch.detekt.rules.empty.EmptyInitBlock
import io.gitlab.arturbosch.detekt.rules.providers.EmptyCodeProvider
import io.gitlab.arturbosch.detekt.rules.style.FileParsingRule
import io.gitlab.arturbosch.detekt.rules.style.WildcardImport
import io.gitlab.arturbosch.detekt.rules.style.optional.OptionalUnit
import io.gitlab.arturbosch.detekt.test.compileForTest
import io.gitlab.arturbosch.detekt.test.loadRuleSet
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

/**
 * @author Artur Bosch
 */
class RunRuleSetWithRuleFiltersSpec : Spek({

    val emptyFile = compileForTest(Case.Empty.path())
    val defaultFile = compileForTest(Case.Default.path())

    describe("RuleSet without MultiRule's") {

        val ruleSet = RuleSet("Test", listOf(WildcardImport(), OptionalUnit()))

        it("filters WildcardImport, runs OptionalUnit") {
            val findings = ruleSet.accept(defaultFile, setOf("WildcardImport"))
            assertThat(findings).allMatch { it.id != "WildcardImport" }
            assertThat(findings).anySatisfy { it.id != "OptionalUnit" }
        }
    }

    describe("MultiRule test cases") {

        fun ruleSet() = loadRuleSet<EmptyCodeProvider>()
        val ruleSetId = EmptyBlocks::class.java.simpleName

        it("should filter by RuleSet id") {
            assertThat(ruleSet().accept(emptyFile, setOf(ruleSetId))).isEmpty()
        }

        it("should filter EmptyInitBlock rule") {
            val ruleIdToFilter = EmptyInitBlock::class.java.simpleName
            assertThat(ruleSet().accept(emptyFile, setOf(ruleIdToFilter))).allMatch { it.id != ruleIdToFilter }
        }
    }

    describe("Mix of MultiRule and normal Rule") {

        it("should filter all rules") {
            val ruleSet = RuleSet("Test", listOf(FileParsingRule(), OptionalUnit()))
            assertThat(ruleSet.accept(emptyFile, setOf("MaxLineLength", "NoTabs", "OptionalUnit"))).isEmpty()
        }
    }
})
