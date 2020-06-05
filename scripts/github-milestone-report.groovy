#!/usr/bin/env groovy
import groovy.cli.commons.CliBuilder
@groovy.lang.Grab('org.kohsuke:github-api:1.112')
import org.kohsuke.github.*

// arguments parsing

def cli = new CliBuilder().tap {
    user(type: String, "Github user or organization. Default: detekt")
    project(type: String, "Github project. Default: detekt")
    milestone(type: int, "Milestone number. Default: latest milestone.")
    h(longOpt: 'help', "Prints this usage.")
}

def options = cli.parse(args)

if (options.h) {
    cli.usage()
    System.exit(0)
}

// formatting helpers

static def entry(issue) {
    entry(issue.title.trim(), issue.number, issue.getHtmlUrl())
}

static def entry(content, issueId, issueUrl) {
    "- $content - [#$issueId]($issueUrl)"
}

static def formatIssues(issues) {
    issues.collect { entry(it) }.join("\n") + "\n"
}

static def footer(footer, url) {
    "See all issues at: [$footer]($url)"
}

static def header(name) {
    "#### $name\n"
}

static def section(name) {
    "##### $name\n"
}

// connect to GitHub

def user = options.user ?: "detekt"
def project = options.project ?: "detekt"
def github = GitHub.connectAnonymously()
def repository = github.getUser(user).getRepository(project)
def milestones = repository
        .listMilestones(GHIssueState.OPEN)
        .sort { it.number }
def milestoneId = options.milestone ?: milestones.last().number

// get milestone and issue data

def milestone = repository.getMilestone(milestoneId)
def issues = repository.getIssues(GHIssueState.ALL, milestone)

def milestoneTitle = milestone.title.trim()
def groups = issues.groupBy { it.labels.find { it.name == "housekeeping" } == null }
def (issuesForUsers, issuesForDevs) = [true, false].collect { groups[it] }

// print report

def content = new StringBuilder().tap {
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
    append(footer(milestoneTitle, milestone.getHtmlUrl()))
}.toString()

println(content)

// write report to disk

def tempFile = File.createTempFile(project, "_$milestone.title")
tempFile.write(content)

println("\nContent saved to $tempFile.path")
