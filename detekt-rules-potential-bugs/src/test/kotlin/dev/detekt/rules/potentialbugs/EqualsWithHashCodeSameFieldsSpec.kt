package dev.detekt.rules.potentialbugs

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EqualsWithHashCodeSameFieldsSpec {
    private val subject = EqualsWithHashCodeSameFields(Config.empty)

    @Nested
    inner class `classes with matching equals and hashCode fields` {

        @Test
        fun `does not report when equals and hashCode use the same fields`() {
            val code = """
                class Person(val name: String, val age: Int) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (other !is Person) return false
                        return name == other.name && age == other.age
                    }

                    override fun hashCode(): Int = name.hashCode() * 31 + age.hashCode()
                }
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report data class`() {
            val code = """
                data class Person(val name: String, val age: Int)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `does not report when class does not override equals and hashCode`() {
            val code = """
                class Person(val name: String, val age: Int)
            """.trimIndent()
            assertThat(subject.lint(code)).isEmpty()
        }
    }

    @Nested
    inner class `classes with mismatched equals and hashCode fields` {

        @Test
        fun `reports when field is used in equals but missing in hashCode`() {
            val code = """
                class Person(val name: String, val age: Int) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (other !is Person) return false
                        return name == other.name && age == other.age
                    }

                    override fun hashCode(): Int = name.hashCode()
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).contains("age")
        }

        @Test
        fun `reports when field is used in hashCode but missing in equals`() {
            val code = """
                class Person(val name: String, val age: Int) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (other !is Person) return false
                        return name == other.name
                    }

                    override fun hashCode(): Int = name.hashCode() * 31 + age.hashCode()
                }
            """.trimIndent()
            val findings = subject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).contains("age")
        }

        @Test
        fun `does not report subset when allowHashCodeSubset is enabled`() {
            val configSubject = EqualsWithHashCodeSameFields(TestConfig("allowHashCodeSubset" to true))
            val code = """
                class Person(val name: String, val age: Int) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (other !is Person) return false
                        return name == other.name && age == other.age
                    }

                    override fun hashCode(): Int = name.hashCode()
                }
            """.trimIndent()
            assertThat(configSubject.lint(code)).isEmpty()
        }

        @Test
        fun `reports when allowHashCodeSubset is enabled but hashCode uses extra field`() {
            val configSubject = EqualsWithHashCodeSameFields(TestConfig("allowHashCodeSubset" to true))
            val code = """
                class Person(val name: String, val age: Int) {
                    override fun equals(other: Any?): Boolean {
                        if (this === other) return true
                        if (other !is Person) return false
                        return name == other.name
                    }

                    override fun hashCode(): Int = name.hashCode() * 31 + age.hashCode()
                }
            """.trimIndent()
            val findings = configSubject.lint(code)
            assertThat(findings).hasSize(1)
            assertThat(findings[0].message).contains("age")
        }
    }
}
