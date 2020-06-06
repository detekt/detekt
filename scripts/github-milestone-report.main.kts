#!/usr/bin/env kotlinc -script --

/**
 * Script to prepare release notes for the upcoming Detekt release
 *
 * You need kotlin 1.3.70+ installed on your machine
 */

@file:Repository("https://kotlin.bintray.com/kotlinx")
@file:DependsOn("org.kohsuke:github-api:1.112")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.2.1")

import org.kohsuke.github.GitHub
import org.kohsuke.github.GHIssue
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GHMilestone
import org.kohsuke.github.GHRepository
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import java.io.File
import java.net.URL

// arguments parsing
val parser = ArgParser("github-milestone-report")

val user by parser.option(
    type = ArgType.String,
    shortName = "u",
    description = "Github user or organization."
).default("detekt")
val project by parser.option(
    type = ArgType.String,
    shortName = "p",
    description = "Github project."
).default("detekt")
val milestone by parser.option(
    type = ArgType.Int,
    shortName = "m",
    description = "Milestone number. Default: latest milestone."
)

parser.parse(args)

// formatting helpers

fun formatIssues(issues: List<GHIssue>?) =
    issues?.joinToString(separator = "\n", postfix = "\n") { entry(it) } ?: ""

fun entry(issue: GHIssue) = entry(issue.title.trim(), issue.number, issue.htmlUrl)

fun entry(content: String, issueId: Int, issueUrl: URL) = "- $content - [#$issueId]($issueUrl)"

fun footer(footer: String, url: URL) = "See all issues at: [$footer]($url)"

fun header(name: String) = "#### $name\n"

fun section(name: String) = "##### $name\n"

// connect to GitHub

val github: GitHub = GitHub.connectAnonymously()
val ghRepository: GHRepository = github.getUser(user).getRepository(project)
val milestones = ghRepository.listMilestones(GHIssueState.OPEN).toMutableList()
milestones.sortBy { it.number }
val milestoneId = milestone ?: milestones.last().number

// get milestone and issue data

val ghMilestone: GHMilestone = ghRepository.getMilestone(milestoneId)
val ghIssues: MutableList<GHIssue> = ghRepository.getIssues(GHIssueState.ALL, ghMilestone)

val milestoneTitle = ghMilestone.title.trim()
val groups = ghIssues.groupBy { issue ->
    issue.labels.any { it.name == "housekeeping" }
}
val (issuesForUsers, issuesForDevs) = groups[false] to groups[true]

// print report

val content = StringBuilder().apply {
    append(header(milestoneTitle))
    append("\n")
    append(section("Notable Changes"))
    append("\n")
    append(section("Migration"))
    append("\n")
    append(section("Changelog"))
    append("\n")
    append(formatIssues(issuesForUsers))
    append("\n")
    append(section("Housekeeping & Refactorings"))
    append("\n")
    append(formatIssues(issuesForDevs))
    append("\n")
    append(footer(milestoneTitle, ghMilestone.htmlUrl))
}.toString()

println(content)

// write report to disk

val tempFile: File = File.createTempFile(project, "_$milestoneId.title")
tempFile.writeText(content)

println("\nContent saved to ${tempFile.path}")
