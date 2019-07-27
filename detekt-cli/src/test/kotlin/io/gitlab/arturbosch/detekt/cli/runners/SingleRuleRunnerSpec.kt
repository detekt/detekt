package io.gitlab.arturbosch.detekt.cli.runners

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.cli.CliArgs
import io.gitlab.arturbosch.detekt.test.resource
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtClass
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Paths

class SingleRuleRunnerSpec : Spek({

    describe("single rule runner") {

        it("should load and run custom rule") {
            val case = Paths.get(resource("cases/Poko.kt"))

            val args = CliArgs().apply {
                val field = this.javaClass.getDeclaredField("input")
                field.isAccessible = true
                field.set(this, case.toString())
                runRule = "test:test"
            }
            // assertion is made inside the custom console report
            SingleRuleRunner(args).execute() // also indirect assertion that test:test exists
        }
    }
})

class TestConsoleReport : ConsoleReport() {
    override fun render(detektion: Detektion): String? {
        assertThat(detektion.findings).hasSize(1)
        return "I've run!"
    }
}

class TestProvider : RuleSetProvider {
    override val ruleSetId: String = "test"
    override fun instance(config: Config): RuleSet = RuleSet(ruleSetId, listOf(TestRule()))
}

class TestRule : Rule() {
    override val issue = Issue("test", Severity.Minor, "", Debt.FIVE_MINS)
    override fun visitClass(klass: KtClass) {
        report(CodeSmell(issue, Entity.from(klass), issue.description))
    }
}
