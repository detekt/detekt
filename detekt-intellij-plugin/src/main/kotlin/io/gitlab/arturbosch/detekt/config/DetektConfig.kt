package io.gitlab.arturbosch.detekt.config

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

/**
 * @author Dmytro Primshyts
 */
class DetektConfig(private val project: Project) : SearchableConfigurable {
	private val detektConfigStorage: DetektConfigStorage = DetektConfigStorage.instance(project)
	private val detektConfigurationForm: DetektConfigurationForm = DetektConfigurationForm()

	override fun isModified(): Boolean = detektConfigurationForm.isModified

	override fun getId(): String = "detekt_config"

	override fun getDisplayName(): String = "Detekt"

	override fun apply() {
		detektConfigurationForm.apply()
		DaemonCodeAnalyzer.getInstance(project)
				.restart()
	}

	override fun reset() = detektConfigurationForm.reset()

	override fun createComponent(): JComponent? = detektConfigurationForm.createPanel(detektConfigStorage)

}
