package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import io.github.classgraph.ClassGraph
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

internal class WrapperSmokeTestSpec {
    @ParameterizedTest(name = "for rule: {0}")
    @MethodSource("ktlintRules")
    fun `smoke test`(subject: KtlintRule) {
        val result = subject.lint(
            """
                fun main() {
                    println("hello world!")
                }
                
            """.trimIndent()
        )

        assertThat(result).isEmpty()
    }

    fun ktlintRules(): List<KtlintRule> =
        ClassGraph()
            .acceptPackages("dev.detekt.rules.ktlintwrapper.wrappers")
            .enableClassInfo()
            .enableClasspath()
            .scan()
            .use { scanResult -> scanResult.getAllSubclasses(KtlintRule::class.java) }
            .map { classInfo ->
                Class.forName(classInfo.getName())
                    .getConstructor(Config::class.java)
                    .newInstance(Config.empty)
                    as KtlintRule
            }
}
