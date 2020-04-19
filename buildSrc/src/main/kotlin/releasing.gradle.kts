plugins {
    id("com.github.breadmoirai.github-release")
}

githubRelease {
    token(project.findProperty("github.token") as? String ?: "")
    owner.set("arturbosch")
    repo.set("detekt")
    overwrite.set(true)
    dryRun.set(true)
    body {
        var changelog = project.file("docs/pages/changelog 1.x.x.md").readText()
        val sectionStart = "#### ${project.version}"
        changelog = changelog.substring(changelog.indexOf(sectionStart) + sectionStart.length)
        changelog = changelog.substring(0, changelog.indexOf("#### 1"))
        changelog.trim()
    }
    val cliBuildDir = project(":detekt-cli").buildDir
    releaseAssets.setFrom(cliBuildDir.resolve("libs/detekt-cli-${project.version}-all.jar"))
    releaseAssets.setFrom(cliBuildDir.resolve("run/detekt"))
}
