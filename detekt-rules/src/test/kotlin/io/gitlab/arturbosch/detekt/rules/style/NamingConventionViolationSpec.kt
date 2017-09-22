package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.rules.style.naming.ClassNaming
import io.gitlab.arturbosch.detekt.rules.style.naming.ConstantNaming
import io.gitlab.arturbosch.detekt.rules.style.naming.EnumNaming
import io.gitlab.arturbosch.detekt.rules.style.naming.FunctionMaxLength
import io.gitlab.arturbosch.detekt.rules.style.naming.FunctionNaming
import io.gitlab.arturbosch.detekt.rules.style.naming.NamingRules
import io.gitlab.arturbosch.detekt.rules.style.naming.PackageNaming
import io.gitlab.arturbosch.detekt.rules.style.naming.VariableNaming
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class NamingConventionViolationSpec : SubjectSpek<NamingRules>({

	subject { NamingRules() }

	it("should find all wrong namings") {
		subject.lint(Case.NamingConventions.path())
		assertThat(subject.findings).hasSize(11)
	}
})

class NamingConventionTest {

	@Test
	fun differentKindsOfConstants() {
		assertThat(NamingRules().lint(
				"""
            const val MY_NAME = "Artur"
            const val MYNAME = "Artur"
            const val MyNAME = "Artur" // positive
            const val serialVersionUID = 42L
            """
		)).hasSize(1)
	}

	@Test
	fun uppercaseAllowedForVariablesInsideObjectDeclaration() {
		assertThat(NamingRules().lint(
				"""
			object Bla {
				val MY_NAME = "Artur"
			}
            """
		)).hasSize(0)
	}

	@Test
	fun upperCasePackageDirectiveName() {
		assertThat(NamingRules().lint("package FOO.BAR")).hasSize(1)
	}

	@Test
	fun upperCamelCasePackageDirectiveName() {
		assertThat(NamingRules().lint("package Foo.Bar")).hasSize(1)
	}

	@Test
	fun camelCasePackageDirectiveName() {
		assertThat(NamingRules().lint("package fOO.bAR")).hasSize(1)
	}

	@Test
	fun correctPackageDirectiveName() {
		assertThat(NamingRules().lint("package foo.bar")).hasSize(0)
	}

	@Test
	fun numbersNotAllowedForEnumEntries() {
		val lint = NamingRules().lint(
				"""
				enum class WorkFlow {
					ACTIVE, NOT_ACTIVE, Unknown, Number1
				}
				"""
		)
		assertThat(lint).hasSize(1)
	}
}

class NamingConventionCustomPattern {

	private val configCustomRules =
			object : Config {
				override fun subConfig(key: String): Config = Config.empty

				@Suppress("UNCHECKED_CAST")
				override fun <T : Any> valueOrDefault(key: String, default: T): T =
						when (key) {
							FunctionNaming.FUNCTION_PATTERN -> "^`.+`$" as T
							ClassNaming.CLASS_PATTERN -> "^aBbD$" as T
							VariableNaming.VARIABLE_PATTERN -> "^123var$" as T
							ConstantNaming.CONSTANT_PATTERN -> "^lowerCaseConst$" as T
							EnumNaming.ENUM_PATTERN -> "^(enum1)|(enum2)$" as T
							PackageNaming.PACKAGE_PATTERN -> "^(package_1)$" as T
							FunctionMaxLength.MAXIMUM_FUNCTION_NAME_LENGTH -> 50 as T
							else -> default
						}
			}
	private val config = object : Config {
		override fun subConfig(key: String): Config =
				when (key) {
					FunctionNaming::class.simpleName -> configCustomRules
					FunctionMaxLength::class.simpleName -> configCustomRules
					ClassNaming::class.simpleName -> configCustomRules
					VariableNaming::class.simpleName -> configCustomRules
					ConstantNaming::class.simpleName -> configCustomRules
					EnumNaming::class.simpleName -> configCustomRules
					PackageNaming::class.simpleName -> configCustomRules
					else -> Config.empty
				}

		override fun <T : Any> valueOrDefault(key: String, default: T): T = default
	}
	private val rule = NamingRules(config)

	@Test
	fun shouldUseCustomNameForMethodAndClass() {
		assertThat(rule.lint("""
            class aBbD{
                fun `name with back ticks`(){
                  val 123var = ""
                }

                companion object {
                  const val lowerCaseConst = ""
                }
            }
        """)).hasSize(0)
	}

	@Test
	fun shouldUseCustomNameForConstant() {
		assertThat(rule.lint("""
            class aBbD{
                companion object {
                  const val lowerCaseConst = ""
                }
            }
        """)).hasSize(0)
	}

	@Test
	fun shouldUseCustomNameForEnum() {
		assertThat(rule.lint("""
            class aBbD{
                enum class aBbD {
                    enum1, enum2
                }
            }
        """)).hasSize(0)
	}

	@Test
	fun shouldUseCustomNameForPackage() {
		assertThat(rule.lint("package package_1")).hasSize(0)
	}
}

class NamingConventionLengthSpec : SubjectSpek<NamingRules>({

	subject { NamingRules() }

	it("should report a variable name that is too short") {
		val code = "private val a = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should report a variable name that is too long") {
		val code = "private val thisVariableIsDefinitelyWayTooLongAndShouldBeMuchShorter = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should not report a variable name that is okay") {
		val code = "private val thisOneIsCool = 3"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}

	it("should report a function name that is too short") {
		val code = "private fun a = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should report a function name that is too long") {
		val code = "private fun thisFunctionIsDefinitelyWayTooLongAndShouldBeMuchShorter = 3"
		subject.lint(code)
		assertThat(subject.findings).hasSize(1)
	}

	it("should not report a function name that is okay") {
		val code = "private fun three = 3"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}

	it("should not report a function name that begins with a backtick, capitals, and spaces") {
		val code = "private fun `Hi bye` = 3"
		subject.lint(code)
		assertThat(subject.findings).isEmpty()
	}
})
