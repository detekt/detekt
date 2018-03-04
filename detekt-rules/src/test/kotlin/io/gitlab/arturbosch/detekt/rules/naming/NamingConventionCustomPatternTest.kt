package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.test.TestConfig
import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NamingConventionCustomPatternTest {

	private val configCustomRules =
			object : Config {
				override fun subConfig(key: String): Config = Config.empty

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
	private val config = object : Config {
		override fun subConfig(key: String): Config =
				when (key) {
					FunctionNaming::class.simpleName -> configCustomRules
					FunctionMaxLength::class.simpleName -> configCustomRules
					ClassNaming::class.simpleName -> configCustomRules
					VariableNaming::class.simpleName -> configCustomRules
					TopLevelPropertyNaming::class.simpleName -> configCustomRules
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
		assertThat(VariableNaming(config).lint(code)).hasSize(0)
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
		assertThat(FunctionNaming(config).lint(code)).hasSize(0)
	}
}
