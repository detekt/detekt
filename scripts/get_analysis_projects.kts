#!kotlinc -script

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit.MINUTES

val githubProjects = listOf(
    "arrow-kt/arrow",
    "pintrest/ktlint",
    "vanniktech/gradle-dependency-graph-generator-plugin",
    "vanniktech/lint-rules",
    "vanniktech/junit-rules",
    "spekframework/spek",
    "Kotlin/kotlinx.coroutines",
    "Kotlin/kotlinx.serialization",
    "Kotlin/kotlinx-datetime",
    "Kotlin/dokka",
    "kotest/kotest",
    "tipsy/javalin",
)

class Downloader(private val basePath: Path, private val project: String) {

    fun download() {
        if (Files.exists(basePath.resolve(project.substringAfter('/')))) {
            println("Skipping $project as it already exists.")
            return
        }
        println("Downloading $project")
        cloneRepo("https://github.com/$project.git")
    }

    private fun cloneRepo(repo: String) {
        ProcessBuilder("git", "clone", repo)
            .directory(basePath.toFile())
            .inheritIO()
            .start()
            .waitFor(MAX_CLONE_WAIT_MINUTES, MINUTES)
    }

    companion object {
        const val MAX_CLONE_WAIT_MINUTES: Long = 5
    }
}

fun downloadAnalysisProjects(basePath: Path) {
    println("Downloading analysis projects to ${basePath.toAbsolutePath()}")
    Files.createDirectories(basePath)

    githubProjects.forEach { project ->
        Downloader(basePath, project).download()
    }
}

if (args.size != 1) {
    println(
        """
            Usage: ./get_analysis_projects.kts [/path/to/storing/folder]
            or kotlinc -script get_analysis_projects.kts [/path/to/storing/folder]
        """.trimIndent()
    )
    System.exit(1)
}

downloadAnalysisProjects(Path.of(args.first()))
