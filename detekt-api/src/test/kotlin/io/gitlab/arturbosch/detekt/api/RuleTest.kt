package io.gitlab.arturbosch.detekt.api

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File
import kotlin.test.assertNotNull

/**
 * @author Artur Bosch
 */
internal class RuleTest : Spek({

	it("rule should be suppressed") {
		val ktFile = compilerFor("SuppressedObject.kt")
		val rule = TestRule()
		rule.visit(ktFile)
		assertNotNull(rule.expected)
	}

})

fun compilerFor(resource: String) = Compiler.compileFromContent(
		File(Compiler.javaClass.getResource("/$resource").path).readText())

internal object Compiler {

	private val psiFileFactory: PsiFileFactory

	init {
		val project = KotlinCoreEnvironment.createForProduction(Disposer.newDisposable(),
				CompilerConfiguration(), EnvironmentConfigFiles.JVM_CONFIG_FILES).project
		psiFileFactory = PsiFileFactory.getInstance(project)
	}

	fun compileFromContent(content: String): KtFile {
		return psiFileFactory.createFileFromText(KotlinLanguage.INSTANCE, content) as KtFile
	}
}

class TestRule : Rule("Test") {
	var expected: String? = "Test"
	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		expected = null
	}
}
