package io.gitlab.arturbosch.detekt.api.internal

import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.net.URLClassLoader

class KotlinEnvironmentUtilsTest : Spek({

    describe("retrieved kotlin language version") {
        it("should match") {
            val expectedVersionString = System.getProperty("kotlinVersion", "")
                .splitToSequence('.')
                .take(2)
                .joinToString(".")

            val classpathUrls = (javaClass.classLoader as? URLClassLoader)?.urLs?.toList()!!
            val classpathFiles = classpathUrls.map { File(it.toURI()) }
            val languageVersion = classpathFiles.getKotlinLanguageVersion()
            assertThat(languageVersion).isNotNull()
            assertThat(languageVersion?.versionString).isEqualTo(expectedVersionString)
        }
    }
})
