package io.gitlab.arturbosch.detekt

import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.YamlConfig
import io.gitlab.arturbosch.detekt.config.DetektConfigStorage
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.nio.file.Paths

/**
 * @author Dmytro Primshyts
 */
class DetektAnnotator : ExternalAnnotator<PsiFile, List<Finding>>() {

	override fun collectInformation(file: PsiFile): PsiFile = file

	override fun doAnnotate(collectedInfo: PsiFile): List<Finding> {
		val configuration = DetektConfigStorage.instance(
				collectedInfo.project
		)

		if (!configuration.enableDetekt) {
			return emptyList()
		}

		if (ProjectRootsUtil.isInTestSource(collectedInfo)
				&& !configuration.checkTestFiles) {
			return emptyList()
		}

		val virtualFile = collectedInfo.originalFile.virtualFile

		val settings = processingSettings(virtualFile, configuration)

		val detektion = DetektFacade.create(settings).run()
		return detektion.findings.flatMap {
			it.value
		}
	}

	override fun apply(file: PsiFile,
					   annotationResult: List<Finding>,
					   holder: AnnotationHolder) {
		annotationResult.forEach {
			holder.createWarningAnnotation(
					it.charPosition.toTextRange(),
					it.messageOrDescription()
			)
		}
	}

	private fun TextLocation.toTextRange(): TextRange = TextRange.create(
			start, end
	)

	private fun processingSettings(virtualFile: VirtualFile,
								   configStorage: DetektConfigStorage): ProcessingSettings {
		return if (configStorage.rulesPath.isEmpty()) {
			ProcessingSettings(
					project = Paths.get(virtualFile.path)
			)
		} else {
			ProcessingSettings(
					project = Paths.get(virtualFile.path),
					config = YamlConfig.load(Paths.get(configStorage.rulesPath))
			)
		}
	}

}
