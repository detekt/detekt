package io.gitlab.arturbosch.detekt.core.settings

import io.gitlab.arturbosch.detekt.core.createProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class EnvironmentFacadeSpec {

    private val classpath = when (File.pathSeparator) {
        ":" -> "/path/to/file1:/path/to/file2:/path/to/file3"
        ";" -> """C:\path\to\file1;C:\path\to\file2;C:\path\to\file3"""
        else -> ""
    }

    @Test
    fun `classpath entries should be separated by platform-specific separator supports ${File_pathSeparator}`() {
        testSettings(classpath).use {
            assertThat(it.classpath).hasSize(3)
        }
    }
}

private fun testSettings(classpath: String) = createProcessingSettings {
    compiler {
        this.classpath = classpath
    }
}
