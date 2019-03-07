package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class LibraryRuleSpec : Spek({

    describe("rule should only run on library files specified by 'includes' in config") {

        val config = TestConfig(mapOf("includes" to "**/**/library/*.kt"))

        it("should run") {
            val path = Case.CasesFolder.path().resolve("library/Library.kt")
            assertThat(DummyRule(config).runAndMark(path)).isTrue()
        }

        it("should not run") {
            assertThat(DummyRule(config).runAndMark(Case.Default.path())).isFalse()
        }
    }

    describe("rule should report on both runs without config") {

        it("should run on library file") {
            val path = Case.CasesFolder.path().resolve("library/Library.kt")
            assertThat(DummyRule().runAndMark(path)).isTrue()
        }

        it("should run on non library file") {
            assertThat(DummyRule().runAndMark(Case.Default.path())).isTrue()
        }
    }
})

class DummyRule(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("test", Severity.CodeSmell, "", Debt.FIVE_MINS)
    private var isDirty: Boolean = false

    override fun visitKtFile(file: KtFile) {
        isDirty = true
    }

    fun runAndMark(path: Path): Boolean {
        this.lint(path)
        return isDirty
    }
}
