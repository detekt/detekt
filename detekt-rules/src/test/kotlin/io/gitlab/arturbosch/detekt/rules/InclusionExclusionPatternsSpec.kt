package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Artur Bosch
 */
class InclusionExclusionPatternsSpec : Spek({

    describe("rule should only run on library file specified by 'includes' pattern") {

        val config = TestConfig(mapOf("includes" to ".*/library/.*.kt"))

        it("should run") {
            Case.Library.path()
                .runWith(DummyRule(config))
                .assertWasVisited()
        }

        it("should not run") {
            Case.Default.path()
                .runWith(DummyRule(config))
                .assertNotVisited()
        }
    }

    describe("rule should only run on library file not matching the specified 'excludes' pattern") {

        val config = TestConfig(mapOf("excludes" to "glob:**/**/Default.kt"))

        it("should run") {
            Case.Library.path()
                .runWith(DummyRule(config))
                .assertWasVisited()
        }

        it("should not run") {
            Case.Default.path()
                .runWith(DummyRule(config))
                .assertNotVisited()
        }
    }

    describe("rule should report on both runs without config") {

        it("should run on library file") {
            Case.Library.path()
                .runWith(DummyRule())
                .assertWasVisited()
        }

        it("should run on non library file") {
            Case.Default.path()
                .runWith(DummyRule())
                .assertWasVisited()
        }
    }

    describe("rule should only run on library file when both patterns are defined") {

        val config = TestConfig(mapOf(
            "includes" to ".*/Library.kt",
            "excludes" to ".*/library/.*"))

        it("should run only on library path") {
            OnlyLibraryTrackingRule(config).apply {
                Files.walk(Case.Library.path().parent)
                    .filter { Files.isRegularFile(it) }
                    .forEach { this.lint(it) }
                assertOnlyLibraryFileVisited()
            }
        }
    }
})

private fun Path.runWith(rule: DummyRule): DummyRule {
    rule.lint(this)
    return rule
}

class OnlyLibraryTrackingRule(config: Config) : Rule(config) {

    override val issue: Issue = Issue("test", Severity.CodeSmell, "", Debt.FIVE_MINS)
    private var libraryFileVisited = false
    private var counter = 0

    override fun visitKtFile(file: KtFile) {
        if ("Library.kt" in file.absolutePath()!!.toString()) {
            libraryFileVisited = true
        } else {
            counter++
        }
    }

    fun assertOnlyLibraryFileVisited() {
        assertThat(counter == 0 && libraryFileVisited).isTrue()
    }
}

class DummyRule(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("test", Severity.CodeSmell, "", Debt.FIVE_MINS)
    private var isDirty: Boolean = false

    override fun visitKtFile(file: KtFile) {
        isDirty = true
    }

    fun assertWasVisited() {
        assertThat(isDirty).isTrue()
    }

    fun assertNotVisited() {
        assertThat(isDirty).isFalse()
    }
}
