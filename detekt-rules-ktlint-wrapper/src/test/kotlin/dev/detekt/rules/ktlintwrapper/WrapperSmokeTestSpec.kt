package dev.detekt.rules.ktlintwrapper

import dev.detekt.api.Config
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.lint
import io.github.classgraph.ClassGraph
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class WrapperSmokeTestSpec {
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
            .scan()
            .use { scanResult -> scanResult.getSubclasses(KtlintRule::class.java).loadClasses() }
            .map { it.getDeclaredConstructor(Config::class.java).newInstance(Config.Empty) as KtlintRule }
}
