package io.github.detekt.parser

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File

class KotlinEnvironmentUtilsTest : Spek({

    describe("retrieved kotlin language version") {
        it("should match") {
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
})
