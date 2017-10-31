package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.Rule

/**
 * @author Marvin Ramin
 */
object ConfigPrinter : DocumentationPrinter<List<RuleSetPage>> {

	override fun print(item: List<RuleSetPage>): String {
		var config = ""
		config += defaultAutoCorrectFailFastConfiguration()
		config += "\n\n"
		config += defaultTestPatternConfiguration()
		config += "\n\n"
		config += defaultBuildConfiguration()
		config += "\n\n"
		config += defaultProcessorsConfiguration()
		config += "\n\n"
		config += defaultConsoleReportsConfiguration()
		config += "\n\n"
		config += defaultOutputReportsConfiguration()
		config += "\n\n"

		config += item.joinToString("\n") { it.print() }
		return config
	}

	private fun RuleSetPage.print(): String {
		var config = ""
		config += "${ruleSet.name}:\n"
		config += "  active: ${ruleSet.active}\n" // Are they active or not?
		rules.forEach {
			config += it.print()
		}
		return config
	}

	private fun Rule.print(): String {
		var config = ""
		config += "  $name:\n"
		config += "    active: $active\n"
		configuration.forEach {
			config += "    ${it.name}: ${it.defaultValue}\n"
		}
		return config
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
