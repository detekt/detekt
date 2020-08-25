#!/bin/sh
//bin/true; exec kotlinc -script "$0" -- "$@"

/**
 * Script to compare two detekt releases by running both versions and diffing the txt results.
 *
 * Working directory is expected to be [path/to/detekt].
 * When running the script from IntelliJ (Ctrl+Shift+F10), you have to change the working directory before.
 * Setup: "Shift+Alt+F10 -> Right -> Edit... -> Working Directory"
 *
 * This script must find '/scripts/compare_releases_config.yml' and the detekt-cli module.
 * It automatically picks up the shadowJar's called "detekt-cli-<version>-all.jar", runs them
 * and uses 'diff' or a given diff tool to compare the results.
 *
 * You need kotlin 1.3.70+ installed on your machine
 */

@file:Suppress("detekt.CommentSpacing") // for the exec line
@file:DependsOn("com.github.ajalt:clikt:2.7.1")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Pattern

class CompareReleases : CliktCommand() {

    private val version1: String by option("-v1", help = "First detekt release version.").required()
    private val version2: String by option("-v2", help = "Second detekt release version.").required()
    private val analysisPath: String by option("-p", help = "Path to a project.").required()
    private val configPath: String by option(
        "-c",
        help = "Path to a config file. Default: scripts/compare_releases_config.yml."
    ).default("scripts/compare_releases_config.yml")
    private val diffTool: String by option("-d", help = "Diff tool. Default: diff.").default("diff")

    override fun run() {
        require(version1 != version2) { "Same version '$version1' used as input." }
        val project = Paths.get(analysisPath).toAbsolutePath().normalize()
        require(Files.exists(project)) { "analysis path '$analysisPath' does not exist." }
        val config = Paths.get(".", configPath).toAbsolutePath().normalize()
        require(Files.exists(config)) { "config at '$configPath' must exist." }

        val rootForJars = Paths.get(".", "detekt-cli/build/libs")
        val jar1 = findJar(rootForJars, version1).toAbsolutePath().normalize()
        val jar2 = findJar(rootForJars, version2).toAbsolutePath().normalize()
        println("Comparing: \n$jar1\n$jar2")

        val diff1 = Files.createTempFile("detekt", "compare")
        val diff2 = Files.createTempFile("detekt", "compare")
        javaExec(jar1, diff1)
        javaExec(jar2, diff2)
        println("Detekt txt results are saved at:\n$diff1\n$diff2")

        performDiff(diff1, diff2)
    }

    private fun findJar(root: Path, version: String): Path {
        val pattern = Pattern.compile("detekt-cli-$version-all.jar").asPredicate()
        return Files.walk(root)
            .filter { pattern.test(it.fileName.toString()) }
            .findFirst()
            .orElseThrow { IllegalArgumentException("no jar with version $version found") }
    }

    private fun javaExec(jar: Path, output: Path) {
        val command = listOf(
            "java",
            "-jar",
            jar.toString(),
            "--input",
            analysisPath,
            "--build-upon-default-config",
            "--fail-fast",
            "--config",
            configPath,
            "--excludes",
            "**/resources/**,**/build/**,**/out/**,**/target/**",
            "--report",
            "txt:$output"
        )
        println("Executing ${command.joinToString(" ")}")
        ProcessBuilder(command)
            .inheritIO()
            .start()
            .waitFor()
    }

    private fun performDiff(diff1: Path, diff2: Path) {
        val command = listOf(diffTool, diff1.toString(), diff2.toString())
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
}

CompareReleases().main(args)
