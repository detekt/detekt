package io.gitlab.arturbosch.detekt.api

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.lastBlockStatementOrThis
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import java.io.File
import kotlin.test.assertEquals
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

	it("findings are suppressed") {
		val ktFile = compilerFor("SuppressedElements.kt")
		val ruleSet = RuleSet("Test", listOf(TestLM(), TestLPL()))
		val findings = ruleSet.accept(ktFile)
		assertEquals(0, findings.size)
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

class TestLM : Rule("LongMethod") {
	override fun visitNamedFunction(function: KtNamedFunction) {
		val start = Location.startLineAndColumn(function.funKeyword!!).line
		val end = Location.startLineAndColumn(function.lastBlockStatementOrThis()).line
		val offset = end - start
		if (offset > 10) addFindings(CodeSmell(id, Entity.from(function)))
	}
}

class TestLPL : Rule("LongParameterList") {
	override fun visitNamedFunction(function: KtNamedFunction) {
		val size = function.valueParameters.size
		if (size > 5) addFindings(CodeSmell(id, Entity.from(function)))
	}
}