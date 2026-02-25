package dev.detekt.gradle

import dev.detekt.gradle.testkit.withResourceDir
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test

class JvmSpec {
    @Test
    fun `Type resolution on JVM`() {
        val result = GradleRunner.create()
            .withResourceDir("jvm")
            .withPluginClasspath()
            .withArguments("detektMain")
            .buildAndFail()

        assertThat(result.output).doesNotContain("unresolved reference")
        assertThat(result.output).contains("failed with 5 issues.")
        assertThat(result.output).contains(
            "src/main/kotlin/Errors.kt:7:9: Do not directly exit the process outside the `main` function. Throw an exception instead. [ExitOutsideMain]",
            "src/main/kotlin/Errors.kt:12:16: Do not directly exit the process outside the `main` function. Throw an exception instead. [ExitOutsideMain]",
            "src/main/kotlin/Caller.kt:5:18: The method `jvm.src.main.kotlin.Callee.forbiddenMethod` has been forbidden in the detekt config. [ForbiddenMethodCall]",
            "src/main/kotlin/Caller.kt:6:9: Callee()?.toString() contains an unnecessary safe call operator [UnnecessarySafeCall]",
            "src/main/kotlin/Caller.kt:7:9: Book.serializer()?.toString() contains an unnecessary safe call operator [UnnecessarySafeCall]",
        )
    }
}
