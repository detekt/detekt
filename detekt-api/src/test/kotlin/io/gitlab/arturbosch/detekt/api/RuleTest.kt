package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiFileFactory
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
		val context = Context()
		val ktFile = compilerFor("SuppressedObject.kt")
		val rule = TestRule()
		rule.visit(context, ktFile)
		assertNotNull(rule.expected)
	}

	it("findings are suppressed") {
		val context = Context()
		val ktFile = compilerFor("SuppressedElements.kt")
		val ruleSet = RuleSet("Test", listOf(TestLM(), TestLPL()))
		ruleSet.accept(context, ktFile)
		assertEquals(0, context.findings.size)
	}

	it("rule should be suppressed by ALL") {
		val context = Context()
		val ktFile = compilerFor("SuppressedByAllObject.kt")
		val rule = TestRule()
		rule.visit(context, ktFile)
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
	override fun visitClassOrObject(context: Context, classOrObject: KtClassOrObject) {
		expected = null
	}
}

class TestLM : Rule("LongMethod") {
	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		val start = Location.startLineAndColumn(function.funKeyword!!).line
		val end = Location.startLineAndColumn(function.lastBlockStatementOrThis()).line
		val offset = end - start
		if (offset > 10) context.report(CodeSmell(ISSUE, Entity.from(function)))
	}

	companion object {
		val ISSUE = Issue("LongMethod")
	}
}

class TestLPL : Rule("LongParameterList") {
	override fun visitNamedFunction(context: Context, function: KtNamedFunction) {
		val size = function.valueParameters.size
		if (size > 5) context.report(CodeSmell(ISSUE, Entity.from(function)))
	}

	companion object {
		val ISSUE = Issue("LongParameterList")
	}
}