@GrabResolver(name='detekt', root='https://dl.bintray.com/arturbosch/code-analysis/', m2Compatible='true')
@Grab('org.vcsreader:vcsreader:1.1.1')
import org.vcsreader.VcsProject
import org.vcsreader.vcs.VcsError
import org.vcsreader.vcs.git.GitVcsRoot

import java.nio.file.Files
import java.nio.file.Paths

HELP_MESSAGE = """
Usage: groovy get_analysis_projects.groovy [/path/to/storing/folder]
"""

def projects = [
		"git@github.com:arrow-kt/arrow.git",
		"git@github.com:shyiko/ktlint.git",
		"git@github.com:vanniktech/gradle-dependency-graph-generator-plugin.git",
		"git@github.com:vanniktech/lint-rules.git",
		"git@github.com:vanniktech/junit-rules.git",
		"git@github.com:spekframework/spek.git",
		"git@github.com:Kotlin/kotlinx.coroutines.git",
		"git@github.com:kotlintest/kotlintest.git",
		"git@github.com:tipsy/javalin.git",
		"git@github.com:arturbosch/ksh.git",
		"git@github.com:arturbosch/kutils.git",
		"git@github.com:arturbosch/deps.git",
		"git@github.com:detekt/detekt.git",
		"git@github.com:detekt/sonar-kotlin.git"
]

if (args.size() == 0) {
	println(HELP_MESSAGE)
	System.exit(1)
}

def storingFolder = Paths.get(args[0])

if (Files.notExists(storingFolder)) {
	Files.createDirectories(storingFolder)
}

def gits = projects.collect { asGitRepo(storingFolder, it) }
new VcsProject(gits) // sets observers, prevents NPE
gits.parallelStream().forEach { handleProject(it) }

static asGitRepo(root, gitUrl) {
	def index = gitUrl.indexOf("/") + 1
	def name = gitUrl.substring(index)
	index = name.indexOf(".")
	name = name.substring(0, index)
	new GitVcsRoot(root.resolve(name).toString(), gitUrl)
}

static handleProject(git) {
	def filePath = Paths.get(git.repoFolder())
	def fileName = filePath.fileName.toString()

	try {
		if (Files.exists(filePath)) {
			def result = git.update()
			if (!result.isSuccessful()) {
				throw new IllegalStateException(extractVcsErrors(result.exceptions()))
			}
			println("Updated existing repo $fileName")
		} else {
			def cloneResult = git.cloneIt()
			if (!cloneResult.isSuccessful()) {
				throw new IllegalStateException(extractVcsErrors(cloneResult.exceptions()))
			}
			println("Finished cloning $fileName")
		}
	} catch (Exception ex) {
		println("Error while handling $fileName: \n$ex.message")
	}
}

static String extractVcsErrors(exceptions) {
	exceptions.stream()
			.filter { VcsError.isInstance(it) }
			.collect { it.message }
			.join("\n")
}
