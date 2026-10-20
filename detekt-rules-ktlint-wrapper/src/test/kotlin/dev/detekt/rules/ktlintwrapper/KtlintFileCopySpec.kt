package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.api.modifiedText
import dev.detekt.rules.ktlintwrapper.wrappers.NoLineBreakBeforeAssignment
import dev.detekt.rules.ktlintwrapper.wrappers.NoSemicolons
import dev.detekt.test.FakeLanguageVersionSettings
import dev.detekt.test.utils.compileContentForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KtlintFileCopySpec {

    @Test
    fun `a file is parsed once and the copy is shared across ktlint rules`() {
        val ktFile = compileContentForTest("fun main() = Unit\n", "SharedCopy.kt")

        NoSemicolons(Config.empty).visitFile(ktFile, FakeLanguageVersionSettings())
        val firstCopy = ktFile.ktlintFileCopy

        NoLineBreakBeforeAssignment(Config.empty).visitFile(ktFile, FakeLanguageVersionSettings())
        val secondCopy = ktFile.ktlintFileCopy

        assertThat(firstCopy).isNotNull()
        assertThat(secondCopy).isSameAs(firstCopy)
    }

    @Test
    fun `the shared copy is rebuilt after modifiedText changes (#9379)`() {
        val ktFile = compileContentForTest("fun main()\n= Unit\n", "RebuiltCopy.kt")
        val rule = NoLineBreakBeforeAssignment(Config.empty)

        // First pass: the file has a violation and a single finding is expected.
        assertThat(rule.visitFile(ktFile, FakeLanguageVersionSettings())).hasSize(1)
        val firstCopy = ktFile.ktlintFileCopy

        // A prior autocorrect cleaned the file; the next visit must rebuild the copy from it.
        ktFile.modifiedText = "fun main() = Unit\n"
        val secondPass = rule.visitFile(ktFile, FakeLanguageVersionSettings())

        assertThat(secondPass).isEmpty()
        assertThat(ktFile.ktlintFileCopy).isNotSameAs(firstCopy)
        assertThat(ktFile.ktlintFileCopy?.text).isEqualTo("fun main() = Unit\n")
    }

    @Test
    fun `a violation reappears when modifiedText reverts to the violating text`() {
        val violating = "fun main()\n= Unit\n"
        val ktFile = compileContentForTest(violating, "Reappear.kt")
        val rule = NoLineBreakBeforeAssignment(Config.empty)

        assertThat(rule.visitFile(ktFile, FakeLanguageVersionSettings())).hasSize(1)

        ktFile.modifiedText = "fun main() = Unit\n"
        assertThat(rule.visitFile(ktFile, FakeLanguageVersionSettings())).isEmpty()

        ktFile.modifiedText = violating
        assertThat(rule.visitFile(ktFile, FakeLanguageVersionSettings())).hasSize(1)
    }
}
