package io.gitlab.arturbosch.detekt.core.settings

import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.createProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal class EnvironmentFacadeSpec : Spek({

    describe("classpath entries can be separated by : or ;") {

        arrayOf(
            "supports ;" to "file1;file2;file3",
            "supports :" to "file1:file2:file3",
            "supports mixing ; and :" to "file1;file2:file3"
        ).forEach { (testName, classpath) ->
            it(testName) {
                testSettings(classpath).use {
                    assertThat(it.classpath).hasSize(3)
                }
            }
        }
    }
})

private fun testSettings(classpath: String) = createProcessingSettings(
    spec = createNullLoggingSpec {
        compiler {
            this.classpath = classpath
        }
    }
)
