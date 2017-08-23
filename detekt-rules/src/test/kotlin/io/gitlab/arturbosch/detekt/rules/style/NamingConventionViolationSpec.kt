package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.Case
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.subject.SubjectSpek
import org.junit.jupiter.api.Test

/**
 * @author Artur Bosch
 */
class NamingConventionViolationSpec : SubjectSpek<NamingConventionViolation>({
	subject { NamingConventionViolation() }

	it("should find all wrong namings") {
		subject.lint(Case.NamingConventions.path())
		assertThat(subject.findings).hasSize(9)
	}

})

class NamingConventionTest {

	@Test
	fun lint() {
		assertThat(NamingConventionViolation().lint(
				"""
            const val MY_NAME = "Artur"
            const val MYNAME = "Artur"
            const val MyNAME = "Artur"
            const val serialVersionUID = 42L
            """
		)).hasSize(1)
	}

	@Test
	fun uppercaseAllowedForVariablesInsideObjectDeclaration() {
		assertThat(NamingConventionViolation().lint(
				"""
			object Bla {
				val MY_NAME = "Artur"
			}
            """
		)).hasSize(0)
	}

	@Test
	fun upperCasePackageDirectiveName() {
		assertThat(NamingConventionViolation().lint("package FOO.BAR")).hasSize(1)
	}

	@Test
	fun upperCamelCasePackageDirectiveName() {
		assertThat(NamingConventionViolation().lint("package Foo.Bar")).hasSize(1)
	}

	@Test
	fun camelCasePackageDirectiveName() {
		assertThat(NamingConventionViolation().lint("package fOO.bAR")).hasSize(1)
	}

	@Test
	fun correctPackageDirectiveName() {
		assertThat(NamingConventionViolation().lint("package foo.bar")).hasSize(0)
	}

	@Test
	fun uppercaseAndUnderscoreAreAllowedLowercaseNotForEnumEntries() {
		val lint = NamingConventionViolation().lint(
				"""
enum class WorkFlow {
    ACTIVE, NOT_ACTIVE, Unknown
}
            """
		)
		lint.forEach { println(it.compact()) }
		assertThat(lint).hasSize(1)
	}
}

class NamingConventionCustomPatter {
	private val configCustomRules =
			object : Config {
				override fun subConfig(key: String): Config = Config.empty

				@Suppress("UNCHECKED_CAST")
				override fun <T : Any> valueOrDefault(key: String, default: T): T =
						when (key) {
							NamingConventionViolation.METHOD_PATTERN -> "^`.+`$" as T
							NamingConventionViolation.CLASS_PATTERN -> "^aBbD$" as T
							NamingConventionViolation.VARIABLE_PATTERN -> "^123var$" as T
							NamingConventionViolation.CONSTANT_PATTERN -> "^lowerCaseConst$" as T
							NamingConventionViolation.ENUM_PATTERN -> "^(enum1)|(enum2)$" as T
							NamingConventionViolation.PACKAGE_PATTERN -> "^(package_1)$" as T
							else -> default
						}
			}
	private val config = object : Config {
		override fun subConfig(key: String): Config =
				if (key == NamingConventionViolation.RULE_SUB_CONFIG) {
					configCustomRules
				} else {
					Config.empty
				}

		override fun <T : Any> valueOrDefault(key: String, default: T): T = default
	}
	private val rule = NamingConventionViolation(config)

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
