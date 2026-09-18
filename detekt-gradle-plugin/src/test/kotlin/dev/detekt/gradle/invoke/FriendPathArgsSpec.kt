package dev.detekt.gradle.invoke

import org.assertj.core.api.Assertions.assertThat
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class FriendPathArgsSpec {
    @Test
    fun `passes the paths that exist`(@TempDir tempDir: File) {
        val existing = File(tempDir, "main.jar").apply { createNewFile() }
        val project = ProjectBuilder.builder().withProjectDir(tempDir).build()

        val actual = FriendPathArgs(project.files(existing)).toArgument()

        assertThat(actual).containsExactly("-Xfriend-paths", existing.absolutePath)
    }

    @Test
    fun `drops a path that does not exist`(@TempDir tempDir: File) {
        val existing = File(tempDir, "main.jar").apply { createNewFile() }
        val missing = File(tempDir, "disabled.jar")
        val project = ProjectBuilder.builder().withProjectDir(tempDir).build()

        val actual = FriendPathArgs(project.files(existing, missing)).toArgument()

        assertThat(actual).containsExactly("-Xfriend-paths", existing.absolutePath)
    }

    @Test
    fun `passes nothing when no path exists`(@TempDir tempDir: File) {
        val missing = File(tempDir, "disabled.jar")
        val project = ProjectBuilder.builder().withProjectDir(tempDir).build()

        val actual = FriendPathArgs(project.files(missing)).toArgument()

        assertThat(actual).isEmpty()
    }

    @Test
    fun `passes nothing when the collection is empty`(@TempDir tempDir: File) {
        val project = ProjectBuilder.builder().withProjectDir(tempDir).build()

        val actual = FriendPathArgs(project.files()).toArgument()

        assertThat(actual).isEmpty()
    }
}
