package io.github.detekt.parser

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class KotlinEnvironmentUtilsSpec {

    @Nested
    inner class `retrieved kotlin language version` {
        @Test
        fun `should match`() {
            val expectedVersionString = System.getProperty("kotlinVersion", "")
                .splitToSequence('.')
                .take(2)
                .joinToString(".")

            val classpathFiles = System.getProperty("testClasspath", "")
                .splitToSequence(';')
                .map(::File)
                .filter(File::exists)
                .toList()

            val languageVersion = classpathFiles.getKotlinLanguageVersion()
            assertThat(languageVersion).isNotNull
            assertThat(languageVersion?.versionString).isEqualTo(expectedVersionString)
        }
    }
}
