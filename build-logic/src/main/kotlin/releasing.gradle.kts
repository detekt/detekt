import com.vdurmont.semver4j.Semver

plugins {
    id("com.github.breadmoirai.github-release")
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    packageGroup.set("io.gitlab.arturbosch")

    repositories {
        create("sonatype") {
            System.getenv("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")?.let { username.set(it) }
            System.getenv("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")?.let { password.set(it) }
        }
    }
}

val version = Versions.currentOrSnapshot()

githubRelease {
    token(providers.gradleProperty("github.token"))
    owner.set("detekt")
    repo.set("detekt")
    overwrite.set(true)
    dryRun.set(false)
    draft.set(true)
    prerelease.set(true)
    targetCommitish.set("main")
    body(
        provider {
            var changelog = project.file("website/src/pages/changelog.md").readText()
            val nextNonBetaVersion = version
            val sectionStart = "#### $nextNonBetaVersion"
            changelog = changelog.substring(changelog.indexOf(sectionStart))
            changelog = changelog.substring(0, changelog.indexOf("#### 1.", changelog.indexOf(sectionStart) + 1))
            changelog.trim()
        }
    )
    val cliBuildDir = project(":detekt-cli").buildDir
    releaseAssets.setFrom(
        cliBuildDir.resolve("libs/detekt-cli-$version-all.jar"),
        cliBuildDir.resolve("distributions/detekt-cli-$version.zip"),
        project(":detekt-formatting").buildDir.resolve("libs/detekt-formatting-$version.jar"),
        project(":detekt-generator").buildDir.resolve("libs/detekt-generator-$version-all.jar"),
        project(":detekt-rules-libraries").buildDir
            .resolve("libs/detekt-rules-libraries-$version.jar"),
        project(":detekt-rules-ruleauthors").buildDir
            .resolve("libs/detekt-rules-ruleauthors-$version.jar")
    )
}

fun updateVersion(increment: (Semver) -> Semver) {
    val versionsFile = file("$rootDir/build-logic/src/main/kotlin/Versions.kt")
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
        fileToUpdate.set(file("$rootDir/website/src/remark/detektVersionReplace.js"))
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
