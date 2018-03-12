package io.gitlab.arturbosch.detekt.config

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

/**
 * @author Dmytro Primshyts
 */
class DetektConfig(private val project: Project): SearchableConfigurable {
	private var detektConfigurationForm: DetektConfigurationForm? = null

	override fun isModified(): Boolean {
		return false
	}

	override fun getId(): String = "detekt_config"

	override fun getDisplayName(): String = "Detekt"

	override fun apply() {

	}

	override fun createComponent(): JComponent? {
		return DetektConfigurationForm(
				DetektConfigStorage.instance(project)
		)
	}
}
