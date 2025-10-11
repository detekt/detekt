package dev.detekt.core.baseline

import dev.detekt.test.utils.resourceAsPath
import dev.detekt.tooling.api.BaselineProvider
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalStateException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.readText

class BaselineFormatSpec {

    @Nested
    inner class `implements tooling api` {

        @Test
        fun `core implements provider`() {
            assertThat(BaselineProvider.load()).isInstanceOf(BaselineFormat::class.java)
        }

        @Test
        fun `reads and writes baselines`(@TempDir tempDir: Path) {
            val provider = BaselineProvider.load()
            val path = resourceAsPath("/baseline_feature/valid-baseline.xml")

            val referenceBaseline = provider.read(path)
            val tempFile = tempDir.resolve("baseline1.xml")
            provider.write(tempFile, referenceBaseline)
            val actualBaseline = provider.read(path)

            assertThat(actualBaseline.currentIssues).containsAll(referenceBaseline.currentIssues)
            assertThat(actualBaseline.manuallySuppressedIssues).containsAll(referenceBaseline.manuallySuppressedIssues)
        }
    }

    @Nested
    inner class `read a baseline file` {

        @Test
        fun `loads the baseline file`() {
            val path = resourceAsPath("/baseline_feature/valid-baseline.xml")
            val (manuallySuppressedIssues, currentIssues) = BaselineFormat().read(path)

            assertThat(manuallySuppressedIssues).hasSize(2)
            assertThat(manuallySuppressedIssues).anySatisfy { it.startsWith("LongParameterList") }
            assertThat(manuallySuppressedIssues).anySatisfy { it.startsWith("LongMethod") }
            assertThat(currentIssues).hasSize(1)
            assertThat(currentIssues).anySatisfy { it.startsWith("FeatureEnvy") }
        }

        @Test
        fun `throws on an invalid baseline file extension`() {
            val path = resourceAsPath("/baseline_feature/invalid-txt-baseline.txt")
            assertThatThrownBy { BaselineFormat().read(path) }
                .isInstanceOf(BaselineFormat.InvalidState::class.java)
        }

        @Test
        fun `throws on an invalid baseline ID declaration`() {
            val path = resourceAsPath("/baseline_feature/missing-temporary-suppressed-baseline.xml")
            assertThatIllegalStateException()
                .isThrownBy { BaselineFormat().read(path) }
                .withMessage("The content of the ID element must not be empty")
        }
    }

    @Nested
    inner class `writes a baseline file` {

        private val savedBaseline = DefaultBaseline(setOf("4", "2", "2"), setOf("1", "2", "3"))

        @Test
        fun `has a new line at the end of the written baseline file`(@TempDir tempDir: Path) {
            val tempFile = tempDir.resolve("baseline1.xml")

            val format = BaselineFormat()
            format.write(tempFile, savedBaseline)
            val content = tempFile.readText()

            assertThat(content).endsWith(">\n")
        }

        @Test
        fun `asserts that the saved and loaded baseline files are equal`(@TempDir tempDir: Path) {
            val tempFile = tempDir.resolve("baseline-saved.xml")

            val format = BaselineFormat()
            format.write(tempFile, savedBaseline)
            val loadedBaseline = format.read(tempFile)

            assertThat(loadedBaseline).isEqualTo(savedBaseline)
        }
    }
}
