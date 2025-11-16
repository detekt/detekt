package dev.detekt.core.settings

import dev.detekt.core.createProcessingSettings
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
            assertThat(it.spec.compilerSpec.classpathEntries()).hasSize(3)
        }
    }

    @Test
    fun `does not fail when classpath contains non-existent files`() {
        val nonExistentPath = "/path/to/nonexistent.jar"
        val mixedClasspath = when (File.pathSeparator) {
            ":" -> "$nonExistentPath:/another/nonexistent.jar"
            ";" -> """$nonExistentPath;C:\another\nonexistent.jar"""
            else -> nonExistentPath
        }

        testSettings(mixedClasspath).use {
            assertThat(it.spec.compilerSpec.classpathEntries()).isNotEmpty
        }
    }
}

private fun testSettings(classpath: String) = createProcessingSettings {
    compiler {
        this.classpath = classpath
    }
}
