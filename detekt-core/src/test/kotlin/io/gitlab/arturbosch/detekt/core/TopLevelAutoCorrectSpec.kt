package io.gitlab.arturbosch.detekt.core

import io.github.detekt.test.utils.NullPrintStream
import io.github.detekt.test.utils.readResourceContent
import io.github.detekt.test.utils.resourceAsPath
import io.github.detekt.test.utils.resourceUrl
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.FileProcessListener
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.core.tooling.AnalysisFacade
import io.gitlab.arturbosch.detekt.core.tooling.DefaultLifecycle
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.psi.KtAnnotation
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

class TopLevelAutoCorrectSpec {

    @Test
    fun `should format the test file but not print the modified content to disc`() {
        val fileContentBeforeAutoCorrect = readResourceContent("cases/Test.kt")
        val fileUnderTest = resourceAsPath("cases/Test.kt")
        val spec = ProcessingSpec {
            project {
                inputPaths = listOf(fileUnderTest)
            }
            config {
                resources = listOf(resourceUrl("configs/rule-and-ruleset-autocorrect-true.yaml"))
            }
            rules {
                autoCorrect = false // fixture
            }
            logging {
                outputChannel = NullPrintStream()
                errorChannel = NullPrintStream()
            }
        }

        val contentChangedListener = object : FileProcessListener {
            override val id: String = "ContentChangedListener"

            override fun onFinish(files: List<KtFile>, result: Detektion) {
                assertThat(files).hasSize(1)
                assertThat(files[0].text).isNotEqualToIgnoringWhitespace(fileContentBeforeAutoCorrect)
            }
        }

        AnalysisFacade(spec).runAnalysis { settings ->
            DefaultLifecycle(
                settings.config,
                settings,
                processorsProvider = { listOf(contentChangedListener) },
                ruleSetsProvider = { listOf(TopLevelAutoCorrectProvider()) }
            )
        }

        assertThat(readResourceContent("cases/Test.kt")).isEqualTo(fileContentBeforeAutoCorrect)
    }
}

private class DeleteAnnotationsRule(config: Config) : Rule(config, "") {
    override fun visitAnnotation(annotation: KtAnnotation) {
        annotation.delete()
    }
}

private class TopLevelAutoCorrectProvider : RuleSetProvider {
    override val ruleSetId = RuleSet.Id("test-rule-set")
    override fun instance() = RuleSet(ruleSetId, listOf(::DeleteAnnotationsRule))
}
