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
        draft.set(true)
        targetCommitish.set("main")
        body {
            var changelog = project.file("website/docs/introduction/changelog 1.x.x.md").readText()
            val nextNonBetaVersion = project.version.toString()
            val sectionStart = "#### $nextNonBetaVersion"
            changelog = changelog.substring(changelog.indexOf(sectionStart))
            changelog = changelog.substring(0, changelog.indexOf("#### 1.", changelog.indexOf(sectionStart) + 1))
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
        fileToUpdate.set(file("${rootProject.rootDir}/website/src/remark/detektVersionReplace.js"))
        linePartToFind.set("const detektVersion = ")
        lineTransformation.set("const detektVersion = \"${Versions.DETEKT}\";")
    }
}

tasks.register("publishToMavenLocal") {
    description = "Publish all the projects to Maven Local"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishToMavenLocal"))
        }
    }
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishToMavenLocal"))
}

tasks.register("publishAllToSonatypeSnapshot") {
    description = "Publish all the projects to Sonatype Snapshot Repository"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishAllPublicationsToSonatypeSnapshotRepository"))
        }
    }
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishAllPublicationsToSonatypeSnapshotRepository"))
}

tasks.register("publishAllToMavenCentral") {
    description = "Publish all the projects to Sonatype Staging Repository"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishAllPublicationsToMavenCentralRepository"))
        }
    }
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishAllPublicationsToMavenCentralRepository"))
}
