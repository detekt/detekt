@groovy.lang.Grab('org.kohsuke:github-api:1.85')
import org.kohsuke.github.*

class Report {
	static def section(header) {
		"#### $header"
	}

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

def section = Report.section(milestone.title.trim()) + "\n"
def issuesString = issues.collect {
	(Report.entry(it.title.trim(), it.number, it.getHtmlUrl()))
}.join("\n") + "\n"
def footer = Report.footer(milestone.title.trim(), milestone.getHtmlUrl())

println(section)
println(issuesString)
println(footer)

println()
def tempFile = File.createTempFile(repo, "_$milestone.title")
tempFile.write("$section\n$issuesString\n$footer")
println("Content saved to $tempFile.path")
