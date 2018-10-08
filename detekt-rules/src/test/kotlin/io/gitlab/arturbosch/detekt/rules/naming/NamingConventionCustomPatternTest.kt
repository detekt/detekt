package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import java.util.regex.PatternSyntaxException

class NamingConventionCustomPatternTest {

	private val configCustomRules =
			object : TestConfig() {
				override fun subConfig(key: String): TestConfig = this

				@Suppress("UNCHECKED_CAST")
				override fun <T : Any> valueOrDefault(key: String, default: T): T =
						when (key) {
							FunctionNaming.FUNCTION_PATTERN -> "^`.+`$" as T
							ClassNaming.CLASS_PATTERN -> "^aBbD$" as T
							VariableNaming.VARIABLE_PATTERN -> "^123var$" as T
							TopLevelPropertyNaming.CONSTANT_PATTERN -> "^lowerCaseConst$" as T
							EnumNaming.ENUM_PATTERN -> "^(enum1)|(enum2)$" as T
							PackageNaming.PACKAGE_PATTERN -> "^(package_1)$" as T
							FunctionMaxLength.MAXIMUM_FUNCTION_NAME_LENGTH -> 50 as T
							else -> default
						}
			}
	private val config = object : TestConfig() {
		override fun subConfig(key: String): TestConfig =
				when (key) {
					FunctionNaming::class.simpleName -> configCustomRules
					FunctionMaxLength::class.simpleName -> configCustomRules
					ClassNaming::class.simpleName -> configCustomRules
					VariableNaming::class.simpleName -> configCustomRules
					TopLevelPropertyNaming::class.simpleName -> configCustomRules
					EnumNaming::class.simpleName -> configCustomRules
					PackageNaming::class.simpleName -> configCustomRules
					else -> this
				}

		override fun <T : Any> valueOrDefault(key: String, default: T): T = default
	}
	private val rule = NamingRules(config)

	private val excludeClassPatternVariableRegexCode = """
			class Bar {
				val MYVar = 3
			}

			object Foo {
				val MYVar = 3
			}"""

	private val excludeClassPatternFunctionRegexCode = """
			class Bar {
				fun MYFun() {}
			}

			object Foo {
				fun MYFun() {}
			}"""

	@Test
	fun shouldUseCustomNameForMethodAndClass() {
		assertThat(rule.lint("""
            class aBbD{
                fun `name with back ticks`(){
                  val `123var` = ""
                }

                companion object {
                  const val lowerCaseConst = ""
                }
            }
        """)).isEmpty()
	}

	@Test
	fun shouldUseCustomNameForConstant() {
		assertThat(rule.lint("""
            class aBbD{
                companion object {
                  const val lowerCaseConst = ""
                }
            }
        """)).isEmpty()
	}

	@Test
	fun shouldUseCustomNameForEnum() {
		assertThat(rule.lint("""
            class aBbD{
                enum class aBbD {
                    enum1, enum2
                }
            }
        """)).isEmpty()
	}

	@Test
	fun shouldUseCustomNameForPackage() {
		assertThat(rule.lint("package package_1")).isEmpty()
	}

	@Test
	fun shouldExcludeClassesFromVariableNaming() {
		val code = """
			class Bar {
				val MYVar = 3
			}

			object Foo {
				val MYVar = 3
			}"""
		val config = TestConfig(mapOf(VariableNaming.EXCLUDE_CLASS_PATTERN to "Foo|Bar"))
		assertThat(VariableNaming(config).lint(code)).isEmpty()
	}

	@Test
	fun shouldNotFailWithInvalidRegexWhenDisabledVariableNaming() {
		val configValues = mapOf(
				"active" to "false",
				VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo"
		)
		val config = TestConfig(configValues)
		assertThat(VariableNaming(config).lint(excludeClassPatternVariableRegexCode)).isEmpty()
	}

	@Test
	fun shouldFailWithInvalidRegexVariableNaming() {
		val config = TestConfig(mapOf(VariableNaming.EXCLUDE_CLASS_PATTERN to "*Foo"))
		assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
			VariableNaming(config).lint(excludeClassPatternVariableRegexCode)
		}
	}

	@Test
	fun shouldExcludeClassesFromFunctionNaming() {
		val code = """
			class Bar {
				fun MYFun() {}
			}

			object Foo {
				fun MYFun() {}
			}"""
		val config = TestConfig(mapOf(FunctionNaming.EXCLUDE_CLASS_PATTERN to "Foo|Bar"))
		assertThat(FunctionNaming(config).lint(code)).isEmpty()
	}

	@Test
	fun shouldNotFailWithInvalidRegexWhenDisabledFunctionNaming() {
		val configRules = mapOf(
				"active" to "false",
				FunctionNaming.EXCLUDE_CLASS_PATTERN to "*Foo"
		)
		val config = TestConfig(configRules)
		assertThat(FunctionNaming(config).lint(excludeClassPatternFunctionRegexCode)).isEmpty()
	}

	@Test
	fun shouldFailWithInvalidRegexFunctionNaming() {
		val config = TestConfig(mapOf(FunctionNaming.EXCLUDE_CLASS_PATTERN to "*Foo"))
		assertThatExceptionOfType(PatternSyntaxException::class.java).isThrownBy {
			FunctionNaming(config).lint(excludeClassPatternFunctionRegexCode)
		}
	}
}
