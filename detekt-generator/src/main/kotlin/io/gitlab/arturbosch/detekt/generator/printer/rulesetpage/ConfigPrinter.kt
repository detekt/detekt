package io.gitlab.arturbosch.detekt.generator.printer.rulesetpage

import io.gitlab.arturbosch.detekt.generator.out.keyValue
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.node
import io.gitlab.arturbosch.detekt.generator.out.yaml
import io.gitlab.arturbosch.detekt.generator.printer.DocumentationPrinter

/**
 * @author Marvin Ramin
 */
object ConfigPrinter : DocumentationPrinter<List<RuleSetPage>> {

	override fun print(item: List<RuleSetPage>): String {
		return yaml {
			yaml { defaultAutoCorrectFailFastConfiguration() }
			emptyLine()
			yaml { defaultTestPatternConfiguration() }
			emptyLine()
			yaml { defaultBuildConfiguration() }
			emptyLine()
			yaml { defaultProcessorsConfiguration() }
			emptyLine()
			yaml { defaultConsoleReportsConfiguration() }
			emptyLine()
			yaml { defaultOutputReportsConfiguration() }
			emptyLine()

			item.sortedBy { it.ruleSet.name }.forEach { (ruleSet, rules) ->
				node(ruleSet.name) {
					keyValue { "active" to "${ruleSet.active}" }

					rules.forEach { rule ->
						node(rule.name) {
							keyValue { "active" to "${rule.active}" }
							rule.configuration.forEach { configuration ->
								if (configuration.defaultValue.isYamlList()) {
									list(configuration.name, configuration.defaultValue.toList())
								} else {
									keyValue { configuration.name to configuration.defaultValue }
								}
							}
						}
					}
					emptyLine()
				}
			}
		}
	}

	private fun defaultAutoCorrectFailFastConfiguration(): String {
		return """
			autoCorrect: true
			failFast: false
			""".trimIndent()
	}

	private fun defaultTestPatternConfiguration(): String {
		return """
			test-pattern: # Configure exclusions for test sources
			  active: true
			  patterns: # Test file regexes
			    - '.*/test/.*'
			    - '.*Test.kt'
			    - '.*Spec.kt'
			  exclude-rule-sets:
			    - 'comments'
			  exclude-rules:
			    - 'NamingRules'
			    - 'WildcardImport'
			    - 'MagicNumber'
			    - 'MaxLineLength'
			    - 'LateinitUsage'
			    - 'StringLiteralDuplication'
			    - 'SpreadOperator'
			    - 'TooManyFunctions'
			""".trimIndent()
	}

	private fun defaultBuildConfiguration(): String {
		return """
			build:
			  warningThreshold: 5
			  failThreshold: 10
			  weights:
			    complexity: 2
			    formatting: 1
			    LongParameterList: 1
			    comments: 1
			""".trimIndent()
	}

	private fun defaultProcessorsConfiguration(): String {
		return """
			processors:
			  active: true
			  exclude:
			  # - 'FunctionCountProcessor'
			  # - 'PropertyCountProcessor'
			  # - 'ClassCountProcessor'
			  # - 'PackageCountProcessor'
			  # - 'KtFileCountProcessor'
			""".trimIndent()
	}

	private fun defaultConsoleReportsConfiguration(): String {
		return """
			console-reports:
			  active: true
			  exclude:
			  #  - 'ProjectStatisticsReport'
			  #  - 'ComplexityReport'
			  #  - 'NotificationReport'
			  #  - 'FindingsReport'
			  #  - 'BuildFailureReport'
			""".trimIndent()
	}

	private fun defaultOutputReportsConfiguration(): String {
		return """
			output-reports:
			  active: true
			  exclude:
			  #  - 'PlainOutputReport'
			  #  - 'XmlOutputReport'
			""".trimIndent()
	}
}

private fun String.isYamlList() = trim().startsWith("-")
private fun String.toList(): List<String> {
	return split("\n").map { it.replace("-", "") }
			.map { it.trim() }
}
