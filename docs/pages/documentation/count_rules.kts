import java.io.File

val root = File(".").absoluteFile

val numberOfRules = root
	.list()
	.map { File(root, it) }
	.flatMap { it.readLines().filter { it.startsWith("### ") } }
	.size

println("#rules: $numberOfRules")
