#!/bin/sh
//bin/true; exec kotlinc -script "$0" -- "$@"

/**
 * Script to prepare release notes for the upcoming detekt release.
 *
 * You need kotlin 1.3.70+ installed on your machine
 */

@file:Suppress("detekt.CommentSpacing") // For the polyglot exec command in line 2.
@file:DependsOn("org.kohsuke:github-api:1.324")
@file:DependsOn("com.github.ajalt:clikt:2.8.0")

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import org.kohsuke.github.GHIssue
import org.kohsuke.github.GHIssueState
import org.kohsuke.github.GHMilestone
import org.kohsuke.github.GHRepository
import org.kohsuke.github.GitHub
import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GithubMilestoneReport : CliktCommand() {

    private val user: String by option("-u", help = "GitHub user or organization. Default: detekt").default("detekt")
    private val project: String by option("-p", help = "GitHub project. Default: detekt").default("detekt")
    private val milestone: Int? by option("-m", help = "Milestone number. Default: latest milestone.").int()
    private val filterExisting: Boolean by option(
        "-f",
        help = "Filter issues that are already in the changelog. Default: false."
    ).flag(default = false)
    private val filterPickRequests: Boolean by option(
        "-r",
        help = "Filter issues labeled with 'pick requests'. Default: false."
    ).flag(default = false)

    @Suppress("LongMethod")
    override fun run() {
        // connect to GitHub
        val github: GitHub = GitHub.connectAnonymously()
        val ghRepository: GHRepository = github.getUser(user).getRepository(project)
        val milestones = ghRepository.listMilestones(GHIssueState.OPEN).toMutableList()
        milestones.sortBy { it.number }
        val milestoneId = milestone ?: milestones.last().number

        // get milestone and issue data

        val ghMilestone: GHMilestone = ghRepository.getMilestone(milestoneId)
        var ghIssues: List<GHIssue> = ghRepository.getIssues(GHIssueState.CLOSED, ghMilestone)
            .filter { it.pullRequest != null }

        if (filterExisting) {
            val changeLogContent = File("./website/src/pages/changelog.md").readText()
            ghIssues = ghIssues.filter { "[#${it.number}]" !in changeLogContent }
        }

        if (filterPickRequests) {
            ghIssues = ghIssues.filter { "pick request" in it.labels.map { it.name } }
        }
        val ghContributors = ghIssues.map { it.user.login }.distinct().sorted()

        val milestoneTitle = ghMilestone.title.trim()
        val groups = ghIssues.groupBy { issue ->
            val labels = issue.labels.map { it.name }
            when {
                "notable changes" in labels -> "notable changes"
                "dependencies" in labels -> "dependencies"
                "housekeeping" in labels -> "housekeeping"
                else -> "changes"
            }
        }
        val notableChanges = groups["notable changes"]
        val dependencyBumps = groups["dependencies"]
        val housekeepingChanges = groups["housekeeping"]
        val issuesForUsers = groups["changes"]

        // print report

        val content = StringBuilder().apply {
            append(header(milestoneTitle))
            append("\n")
            append(section("Notable Changes"))
            append("\n")
            append(formatIssues(notableChanges))
            append("\n")
            append(section("Migration"))
            append("\n")
            append(section("Changelog"))
            append("\n")
            append(formatIssues(issuesForUsers))
            append("\n")
            append(section("Dependency Updates"))
            append("\n")
            append(formatIssues(dependencyBumps))
            append("\n")
            append(section("Housekeeping & Refactorings"))
            append("\n")
            append(formatIssues(housekeepingChanges))
            append("\n")
            append(section("Contributors"))
            append("\n")
            append(formatContributors(ghContributors))
            append("\n")
            append("\n")
            append(footer(milestoneTitle, ghMilestone.htmlUrl))
        }.toString()

        println(content)

        // write report to disk

        val tempFile: File = File.createTempFile(project, "_$milestoneId.$milestoneTitle")
        tempFile.writeText(content)

        println("\nContent saved to ${tempFile.path}")
    }

    private fun formatContributors(ghContributors: List<String>): String {
        val formattedContributors = ghContributors
            .filterNot { it == "renovate[bot]" }
            .joinToString(", ") { "@$it" }
        return "We would like to thank the following contributors that " +
            "made this release possible: $formattedContributors"
    }

    // formatting helpers

    private fun formatIssues(issues: List<GHIssue>?) =
        issues?.joinToString(separator = "\n", postfix = "\n") { entry(it) } ?: ""

    private fun entry(issue: GHIssue) = entry(issue.title.trim(), issue.number, issue.htmlUrl)

    private fun entry(content: String, issueId: Int, issueUrl: URL) = "- $content - [#$issueId]($issueUrl)"

    private fun footer(footer: String, url: URL) = "See all issues at: [$footer]($url)"

    private fun header(name: String) = "#### $name - ${DateTimeFormatter.ISO_DATE.format(LocalDate.now())} \n"

    private fun section(name: String) = "##### $name\n"
}

GithubMilestoneReport().main(args)
