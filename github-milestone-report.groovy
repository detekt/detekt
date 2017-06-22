/**
 * @author Artur Bosch
 */

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

if (args.size() < 3) throw new IllegalArgumentException("Usage: [userId] [repositoryId] [milestoneId]")

def user = args[0] ?: "arturbosch"
def repo = args[1] ?: "detekt"
def mId = args[2].toInteger() ?: 8

def github = GitHub.connectAnonymously()
def repository = github.getUser(user).getRepository(repo)
def milestone = repository.getMilestone(mId)
def issues = repository.getIssues(GHIssueState.ALL, milestone)

def section = Report.section(milestone.title) + "\n"
def issuesString = issues.collect {
	(Report.entry(it.title, it.number, it.url))
}.join("\n") + "\n"
def footer = Report.footer(milestone.title, milestone.url)

println(section)
println(issuesString)
println(footer)

println()
def tempFile = File.createTempFile(repo, "_$milestone.title")
tempFile.write("$section\n$issuesString\n$footer")
println("Content saved to $tempFile.path")