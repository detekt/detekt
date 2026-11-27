package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.assertj.assertThat
import dev.detekt.test.junit.KotlinCoreEnvironmentTest
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.KotlinEnvironmentContainer
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class ForbiddenPositionBasedDestructuringSpec(private val env: KotlinEnvironmentContainer) {
    private val subject = ForbiddenPositionBasedDestructuring(Config.empty)
    private val settings = LanguageVersionSettingsImpl(
        LanguageVersion.LATEST_STABLE,
        ApiVersion.LATEST_STABLE,
        specificFeatures = mapOf(LanguageFeature.NameBasedDestructuring to LanguageFeature.State.ENABLED),
    )

    @Test
    fun `reports explicit positional destructuring in declarations loops and lambdas`() {
        val code = """
            data class Point(val x: Int, val y: Int)
            fun use(points: List<Point>) {
                val [x, y] = points.first()
                for ([a, b] in points) println(a + b)
                points.forEach { [a, b] -> println(a + b) }
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, languageVersionSettings = settings)).hasSize(3)
    }

    @Test
    fun `allows Pair Triple and parenthesized destructuring`() {
        val code = """
            data class Point(val x: Int, val y: Int)
            fun use(point: Point) {
                val [a, b] = Pair(1, 2)
                val [c, d, e] = Triple(1, 2, 3)
                val pairs = listOf(Pair(1, 2))
                for ([left, right] in pairs) println(left + right)
                pairs.forEach { [left, right] -> println(left + right) }
                val (x, y) = point
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, languageVersionSettings = settings)).isEmpty()
    }

    @Test
    fun `reports a type parameter bounded by a forbidden class`() {
        val code = """
            data class Point(val x: Int, val y: Int)
            fun <T : Point> use(point: T) {
                val [x, y] = point
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, languageVersionSettings = settings)).hasSize(1)
    }

    @Test
    fun `allows type parameters bounded by an allowed class`() {
        val code = """
            fun <T : Pair<Int, Int>> direct(pair: T) {
                val [a, b] = pair
            }
            fun <T : Pair<Int, Int>, U : T> indirect(pair: U) {
                val [a, b] = pair
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, languageVersionSettings = settings)).isEmpty()
    }

    @Test
    fun `honors a configured bound when a type parameter has multiple constraints`() {
        val code = """
            package example
            interface Tagged
            interface Coordinate {
                operator fun component1(): Int
                operator fun component2(): Int
            }
            fun <T> use(point: T) where T : Tagged, T : Coordinate {
                val [x, y] = point
            }
        """.trimIndent()
        assertThat(subject.lintWithContext(env, code, languageVersionSettings = settings)).hasSize(1)

        val rule = ForbiddenPositionBasedDestructuring(TestConfig("allowedClasses" to listOf("example.Coordinate")))
        assertThat(rule.lintWithContext(env, code, languageVersionSettings = settings)).isEmpty()
    }

    @Test
    fun `uses fully qualified names from configuration`() {
        val code = """
            package example
            typealias Coordinate = Point
            data class Point(val x: Int, val y: Int)
            fun use(point: Coordinate) {
                val [x, y] = point
                val [a, b] = Pair(1, 2)
            }
        """.trimIndent()
        val rule = ForbiddenPositionBasedDestructuring(TestConfig("allowedClasses" to listOf("example.Point")))
        val findings = rule.lintWithContext(env, code, languageVersionSettings = settings)
        assertThat(findings).hasSize(1)
        assertThat(findings.single()).hasStartSourceLocation(line = 6, column = 5)
    }
}
