import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

val arguments = args.toList()
@Suppress("detekt.MagicNumber")
check(arguments.size >= 3) { "Usage: [version1] [version2] [analysis-path] [diff-tool]?" }

val analysisPath = Paths.get(arguments[2]).toAbsolutePath().normalize()
check(Files.exists(analysisPath)) { "analysis path '$analysisPath' does not exist" }

val configPath = Paths.get(".", "scripts/compare_releases_config.yml")
        .toAbsolutePath().normalize()
check(Files.exists(configPath)) { "config at '$configPath' must exist" }

// the diff tool is expected to exist and accept two files
// default is to use 'diff' which should exist on unix systems
val diffTool = if (arguments.size == 4) arguments[3] else "diff"

fun findJar(root: Path, version: String): Path {
    val pattern = Pattern.compile("detekt-cli-$version-all.jar").asPredicate()
    return Files.walk(root)
            .filter { pattern.test(it.fileName.toString()) }
            .findFirst()
            .orElseThrow { IllegalArgumentException("no jar with version $version found") }
}

val rootForJars = Paths.get(".", "detekt-cli/build/libs")

val version1 = arguments[0]
val version2 = arguments[1]
val jar1 = findJar(rootForJars, version1).toAbsolutePath().normalize()
val jar2 = findJar(rootForJars, version2).toAbsolutePath().normalize()

println("Comparing: \n$jar1\n$jar2")

fun javaExec(jar: Path, output: Path) {
    val command = listOf(
            "java",
            "-jar",
            jar.toString(),
            "--input",
            analysisPath.toString(),
            "--config",
            configPath.toString(),
            "--filters",
            ".*/resources/.*,.*/build/.*,.*/out/.*,.*/test/.*",
            "--report",
            "txt:$output"
    )
    println("Executing ${command.joinToString(" ")}")
    ProcessBuilder(command)
            .inheritIO()
            .start()
            .waitFor()
}

val diff1 = Files.createTempFile("detekt", "compare")
val diff2 = Files.createTempFile("detekt", "compare")

fun executeDetekt() {
    javaExec(jar1, diff1)
    javaExec(jar2, diff2)
    println("Detekt txt results are saved at:\n$diff1\n$diff2")
}

executeDetekt()

fun performDiff() {
    val command = listOf("diff", diff1.toString(), diff2.toString())
    val diffResult = Files.createTempFile("detekt", "diff").toFile()
    ProcessBuilder(command)
            .redirectOutput(diffResult)
            .start()
            .waitFor()
    val diff = diffResult.readText().trim()
    if (diff.isNotEmpty()) {
        println(diff)
        println("There were differences beween results.")
    } else {
        println("No differences between results.")
    }
}

performDiff()
