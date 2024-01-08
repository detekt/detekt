package io.gitlab.arturbosch.detekt.api.internal

import io.github.detekt.psi.absolutePath
import io.github.detekt.test.utils.compileContentForTest
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class InclusionExclusionPatternsSpec {

    @Nested
    inner class `rule should only run on library file specified by 'includes' pattern` {

        private val config = TestConfig(Config.INCLUDES_KEY to listOf("**/library/*.kt"))

        @Test
        fun `should run`() {
            compileContentForTest("", path = Path("/library/Library.kt"))
                .runWith(DummyRule(config))
                .assertWasVisited()
        }

        @Test
        fun `should not run`() {
            compileContentForTest("", path = Path("/Default.kt"))
                .runWith(DummyRule(config))
                .assertNotVisited()
        }
    }

    @Nested
    inner class `rule should only run on library file not matching the specified 'excludes' pattern` {

        private val config = TestConfig(Config.EXCLUDES_KEY to listOf("glob:**/Default.kt"))

        @Test
        fun `should run`() {
            compileContentForTest("", path = Path("/library/Library.kt"))
                .runWith(DummyRule(config))
                .assertWasVisited()
        }

        @Test
        fun `should not run`() {
            compileContentForTest("", path = Path("/Default.kt"))
                .runWith(DummyRule(config))
                .assertNotVisited()
        }
    }

    @Nested
    inner class `pattern should only check the relative path` {
        private val config = TestConfig(Config.EXCLUDES_KEY to listOf("glob:**/library/*.kt"))

        @Test
        fun `should not run`() {
            compileContentForTest("", path = Path("/library/Test.kt"))
                .runWith(DummyRule(config))
                .assertNotVisited()
        }

        @Test
        fun `should run`() {
            compileContentForTest("", basePath = Path("/library/"), path = Path("/library/Test.kt"))
                .runWith(DummyRule(config))
                .assertWasVisited()
        }
    }

    @Nested
    inner class `rule should report on both runs without config` {

        @Test
        fun `should run on library file`() {
            compileContentForTest("", path = Path("/library/Library.kt"))
                .runWith(DummyRule())
                .assertWasVisited()
        }

        @Test
        fun `should run on non library file`() {
            compileContentForTest("", path = Path("/Default.kt"))
                .runWith(DummyRule())
                .assertWasVisited()
        }
    }

    @Nested
    inner class `rule should only run on included files` {
        private val files = listOf("/library/Library.kt", "/library/Dummy.kt", "/library/Dummy2.kt")

        @Test
        fun `should only run on dummies`() {
            val config = TestConfig(
                Config.INCLUDES_KEY to listOf("**/library/**"),
                Config.EXCLUDES_KEY to listOf("**Library.kt"),
            )

            OnlyLibraryTrackingRule(config).apply {
                files.forEach { this.lint(compileContentForTest("", path = Path(it))) }
                assertOnlyLibraryFileVisited(false)
                assertCounterWasCalledTimes(2)
            }
        }

        @Test
        fun `should only run on library file`() {
            val config = TestConfig(
                Config.INCLUDES_KEY to listOf("**/library/**"),
                Config.EXCLUDES_KEY to listOf("**Dummy*.kt"),
            )

            OnlyLibraryTrackingRule(config).apply {
                files.forEach { this.lint(compileContentForTest("", path = Path(it))) }
                assertOnlyLibraryFileVisited(true)
                assertCounterWasCalledTimes(0)
            }
        }
    }
}

private fun KtFile.runWith(rule: DummyRule): DummyRule {
    rule.lint(this)
    return rule
}

private class OnlyLibraryTrackingRule(config: Config) : Rule(config, "") {
    private var libraryFileVisited = false
    private var counter = 0

    override fun visitKtFile(file: KtFile) {
        if ("Library.kt" in file.absolutePath().toString()) {
            libraryFileVisited = true
        } else {
            counter++
        }
    }

    fun assertOnlyLibraryFileVisited(wasVisited: Boolean) {
        assertThat(libraryFileVisited).isEqualTo(wasVisited)
    }

    fun assertCounterWasCalledTimes(size: Int) {
        assertThat(counter).isEqualTo(size)
    }
}

private class DummyRule(config: Config = Config.empty) : Rule(config, "") {
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
