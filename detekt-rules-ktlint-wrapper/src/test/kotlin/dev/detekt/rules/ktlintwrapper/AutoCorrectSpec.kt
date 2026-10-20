package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.api.modifiedText
import dev.detekt.rules.ktlintwrapper.wrappers.NoLineBreakBeforeAssignment
import dev.detekt.rules.ktlintwrapper.wrappers.NoSemicolons
import dev.detekt.test.FakeLanguageVersionSettings
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AutoCorrectSpec {
    @Test
    fun `a subsequent visit to a reused KtFile must not be served stale context after modifiedText changes (#9379)`() {
        // Exercise an unrelated rule on a different file first, so the engine has already built and
        // cached walk state before the file under test is visited.
        val otherFile = compileContentForTest("fun other() = Unit\n", "EngineStaleOther.kt")
        NoSemicolons(Config.empty).visitFile(otherFile, FakeLanguageVersionSettings())

        val ktFile = compileContentForTest("fun main()\n= Unit\n", "EngineStaleContext.kt")
        val rule = NoLineBreakBeforeAssignment(Config.empty)

        // First pass: the file has a violation and a single finding is expected.
        assertThat(rule.visitFile(ktFile, FakeLanguageVersionSettings())).hasSize(1)

        // Simulate a prior autocorrect having cleaned the file.
        ktFile.modifiedText = "fun main() = Unit\n"

        // Second pass: this should read the modified (now clean) file and report nothing.
        val secondPass = rule.visitFile(ktFile, FakeLanguageVersionSettings())

        assertThat(secondPass).isEmpty()
    }
}
