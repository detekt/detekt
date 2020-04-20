@groovy.lang.Grab('org.kohsuke:github-api:1.111')
import org.kohsuke.github.*

final class Report {

    static def entry(content, issueId, issueUrl) {
        "- $content - [#$issueId]($issueUrl)"
    }

    static def footer(footer, url) {
        "See all issues at: [$footer]($url)"
    }
}

if (args.size() > 3) throw new IllegalArgumentException("Usage: [userId] [repositoryId] [milestoneId]")

def user = args.size() > 0 ? args[0] : "arturbosch"
def repo = args.size() > 1 ? args[1] : "detekt"

def github = GitHub.connectAnonymously()
def repository = github.getUser(user).getRepository(repo)

def milestones = repository.listMilestones(GHIssueState.OPEN)
def sortedMilestones = milestones.sort { it.number }
def mId = args.size() > 2 ? args[2].toInteger() : sortedMilestones.last().number
def milestone = repository.getMilestone(mId)
def issues = repository.getIssues(GHIssueState.ALL, milestone)

def header = milestone.title.trim()
def issuesString = issues.collect { (Report.entry(it.title.trim(), it.number, it.getHtmlUrl())) }.join("\n") + "\n"
def footer = Report.footer(header, milestone.getHtmlUrl())

println("#### $header\n")
println("##### Notable Changes\n\n")
println("##### Migration\n\n")
println("##### Changelog\n")
println(issuesString)
println(footer)

println()
def tempFile = File.createTempFile(repo, "_$milestone.title")
tempFile.write("${("#### ${header}" + "\n")}\n$issuesString\n$footer")
println("Content saved to $tempFile.path")
