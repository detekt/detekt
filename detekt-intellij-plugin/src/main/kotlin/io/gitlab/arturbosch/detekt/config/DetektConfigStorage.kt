package io.gitlab.arturbosch.detekt.config

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.annotations.Tag

/**
 * @author Dmytro Primshyts
 */
@State(
		name = "DetektProjectConfiguration",
		storages = [(Storage(StoragePathMacros.WORKSPACE_FILE))]
)
class DetektConfigStorage : PersistentStateComponent<DetektConfigStorage> {

	@Tag
	var enableDetekt: Boolean = false

	@Tag
	var checkTestFiles: Boolean = false

	@Tag
	var rulesPath: String = ""

	override fun getState(): DetektConfigStorage? = this

	override fun loadState(state: DetektConfigStorage) {
		this.enableDetekt = state.enableDetekt
		this.checkTestFiles = state.checkTestFiles
		this.rulesPath = state.rulesPath
	}

	companion object {

		/**
		 * Get instance of [DetektConfigStorage] for given project.
		 *
		 * @param project the project
		 */
		fun instance(project: Project): DetektConfigStorage =
				ServiceManager.getService(DetektConfigStorage::class.java)

	}

}
