package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_CLASSES_KEY
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_FIELDS_KEY
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_FILES_KEY
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_METHODS_KEY
import io.gitlab.arturbosch.detekt.core.NUMBER_OF_PACKAGES_KEY

/**
 * @author Artur Bosch
 */
class ProjectStatisticsReport : ConsoleReport() {

	override fun render(detektion: Detektion): String? {
		return with(StringBuilder()) {
			append("Project Statistics:".format())
			detektion.getData(NUMBER_OF_FILES_KEY)?.let { append("#files: $it".format(PREFIX)) }
			detektion.getData(NUMBER_OF_PACKAGES_KEY)?.let { append("#packages: $it".format(PREFIX)) }
			detektion.getData(NUMBER_OF_CLASSES_KEY)?.let { append("#classes: $it".format(PREFIX)) }
			detektion.getData(NUMBER_OF_METHODS_KEY)?.let { append("#methods: $it".format(PREFIX)) }
			detektion.getData(NUMBER_OF_FIELDS_KEY)?.let { append("#fields: $it".format(PREFIX)) }
			toString()
		}
	}
}
