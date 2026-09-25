package dev.detekt.generator

import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.FileNotFound
import com.github.ajalt.clikt.core.MissingOption
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.parameters.options.NullableOption
import com.github.ajalt.clikt.parameters.options.OptionWithValues
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText

class GeneratorArgs : CoreCliktCommand(name = "detekt-generator") {

    val inputPath: List<Path> by option(
        "-i",
        "--input",
        help = "Input paths to analyze.",
    )
        .path(mustExist = true)
        .splitBatch(",", ";", required = true)

    val documentationPath: Path? by option(
        "-d",
        "--documentation",
        help = "Output path for generated documentation.",
    ).path(canBeFile = false, canBeDir = true)

    val configPath: Path? by option(
        "-c",
        "--config",
        help = "Output path for generated detekt config.",
    ).path(canBeFile = false, canBeDir = true)

    init {
        context {
            readArgumentFile = {
                try {
                    Path(it).readText()
                } catch (_: IOException) {
                    throw FileNotFound(it)
                }
            }
            exitProcess = { exitProcess(it) }
        }
    }

    override fun run() {
        val generator = Generator(
            inputPaths = inputPath,
            documentationPath = documentationPath,
            configPath = configPath,
        )
        generator.execute()
    }
}

private fun <EachT : Any, ValueT> NullableOption<EachT, ValueT>.splitBatch(
    vararg delimiters: String,
    default: () -> List<ValueT> = { emptyList() },
    required: Boolean = false,
): OptionWithValues<List<ValueT>, List<ValueT>, ValueT> =
    copy(
        transformValue = transformValue,
        transformEach = { it },
        transformAll = { calls ->
            when {
                calls.isEmpty() && required -> throw MissingOption(option)
                calls.isEmpty() && !required -> default()
                else -> calls.flatten()
            }
        },
        validator = {},
        nvalues = 1..1,
        valueSplit = { it.split(*delimiters) }
    )
