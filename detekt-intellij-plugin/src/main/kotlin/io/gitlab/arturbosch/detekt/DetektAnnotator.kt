package io.gitlab.arturbosch.detekt

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.core.DetektFacade
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import java.nio.file.Paths

/**
 * @author Dmytro Primshyts
 */
class DetektAnnotator : ExternalAnnotator<PsiFile, List<Finding>>() {

	override fun collectInformation(file: PsiFile): PsiFile = file

	override fun doAnnotate(collectedInfo: PsiFile): List<Finding> {
		val virtualFile = collectedInfo.originalFile.virtualFile

		val settings = ProcessingSettings(
				project = Paths.get(virtualFile.path)
		)

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
}
