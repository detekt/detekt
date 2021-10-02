import com.vdurmont.semver4j.Semver

plugins {
    id("com.github.breadmoirai.github-release")
    id("io.codearte.nexus-staging")
}

nexusStaging {
    packageGroup = "io.gitlab.arturbosch"
    stagingProfileId = "1d8efc8232c5c"
    username = findProperty("sonatypeUsername")
        ?.toString()
        ?: System.getenv("MAVEN_CENTRAL_USER")
    password = findProperty("sonatypePassword")
        ?.toString()
        ?: System.getenv("MAVEN_CENTRAL_PW")
}

project.afterEvaluate {
    githubRelease {
        token(project.findProperty("github.token") as? String ?: "")
        owner.set("detekt")
        repo.set("detekt")
        overwrite.set(true)
        dryRun.set(false)
        targetCommitish.set("main")
        body {
            var changelog = project.file("docs/pages/changelog 1.x.x.md").readText()
            val nextNonBetaVersion = project.version.toString().substringBeforeLast("-")
            val sectionStart = "#### $nextNonBetaVersion"
            changelog = changelog.substring(changelog.indexOf(sectionStart) + sectionStart.length)
            changelog = changelog.substring(0, changelog.indexOf("#### 1."))
            changelog.trim()
        }
        val cliBuildDir = project(":detekt-cli").buildDir
        releaseAssets.setFrom(
            files(
                cliBuildDir.resolve("libs/detekt-cli-${project.version}-all.jar"),
                cliBuildDir.resolve("distributions/detekt-cli-${project.version}.zip"),
                project(":detekt-formatting").buildDir.resolve("libs/detekt-formatting-${project.version}.jar")
            )
        )
    }
}

fun updateVersion(increment: (Semver) -> Semver) {
    val versionsFile = file("${rootProject.rootDir}/build-logic/src/main/kotlin/Versions.kt")
    val newContent = versionsFile.readLines()
        .joinToString("\n") {
            if (it.contains("const val DETEKT: String")) {
                val oldVersion = it.substringAfter("\"").substringBefore("\"")
                val newVersion = Semver(oldVersion).let(increment)
                println("Next release: $newVersion")
                """    const val DETEKT: String = "$newVersion""""
            } else {
                it
            }
        }
    versionsFile.writeText("$newContent\n")
}

tasks {
    register("incrementPatch") { doLast { updateVersion { it.nextPatch() } } }
    register("incrementMinor") { doLast { updateVersion { it.nextMinor() } } }
    register("incrementMajor") { doLast { updateVersion { it.nextMajor() } } }

    register<UpdateVersionInFileTask>("applyDocVersion") {
        fileToUpdate.set(file("${rootProject.rootDir}/docs/_config.yml"))
        linePartToFind.set("detekt_version:")
        lineTransformation.set("detekt_version: ${Versions.DETEKT}")
    }

    register<UpdateVersionInFileTask>("applySelfAnalysisVersion") {
        fileToUpdate.set(file("${rootProject.rootDir}/gradle/libs.versions.toml"))
        linePartToFind.set("detekt-gradle = \"io.gitlab.arturbosch.detekt:detekt-gradle-plugin")
        lineTransformation.set(
            "detekt-gradle = \"io.gitlab.arturbosch.detekt:" +
                "detekt-gradle-plugin:${Versions.DETEKT}\""
        )
    }
}
