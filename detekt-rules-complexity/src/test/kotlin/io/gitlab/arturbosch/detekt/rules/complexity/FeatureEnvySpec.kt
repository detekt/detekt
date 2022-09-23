package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class FeatureEnvySpec(private val env: KotlinCoreEnvironment) {

    val subject = FeatureEnvy()

    @Test
    fun `detect feature envy in standard example`() {
        val code = """
            data class ContactInfo(
                val city: String,
                val postalCode: String,
                val street: String,
                val number: String
            )
            
            class User(val contactInfo: ContactInfo) {
            
                fun prettyPrintAddress() {
                    val prettyAddress = buildString { 
                        append(contactInfo.postalCode)
                        append(" ")
                        append(contactInfo.city)
                        append("\n")
                        append(contactInfo.street)
                        append(" ")
                        append(contactInfo.number)
                    }
                    println(prettyAddress)
                }
            
            }
        """.trimIndent()

        Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }

    @Test
    fun `rectangle sample`() {
        val code = """
            data class Rectangle(val width: Int, val height: Int)

            class RectangleUsageSite(val rectangle: Rectangle) {
                fun printArea() {
                    val area = rectangle.width * rectangle.height
                    println("The area is: \${'$'}{area}")
                }
            }
        """.trimIndent()

        Assertions.assertThat(subject.compileAndLintWithContext(env, code)).isEmpty()
    }

    @Test
    fun `cube sample`() {
        val code = """
            data class Cube(val width: Int, val length: Int, val height: Int)
            
            class CubeUsageSite(val cube: Cube) {
                fun printVolume() {
                    val volume = cube.width * cube.length * cube.height
                    println("The volume is: \${'$'}{volume}")
                }
            }
        """.trimIndent()

        Assertions.assertThat(subject.compileAndLintWithContext(env, code)).hasSize(1)
    }
}
