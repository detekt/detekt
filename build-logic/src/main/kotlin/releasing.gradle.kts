import com.vdurmont.semver4j.Semver

plugins {
    id("com.github.breadmoirai.github-release")
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    packageGroup = "io.gitlab.arturbosch"

    repositories {
        create("sonatype") {
            System.getenv("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")?.let { username = it }
            System.getenv("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")?.let { password = it }
        }
    }
}

val version = Versions.currentOrSnapshot()

githubRelease {
    token(providers.gradleProperty("github.token"))
    owner = "detekt"
    repo = "detekt"
    overwrite = true
    dryRun = false
    draft = true
    prerelease = true
    targetCommitish = "main"
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
    val cliBuildDir = project(":detekt-cli").layout.buildDirectory
    releaseAssets.setFrom(
        cliBuildDir.file("libs/detekt-cli-$version-all.jar"),
        cliBuildDir.file("distributions/detekt-cli-$version.zip"),
        project(":detekt-formatting").layout.buildDirectory.file("libs/detekt-formatting-$version.jar"),
        project(":detekt-generator").layout.buildDirectory.file("libs/detekt-generator-$version-all.jar"),
        project(":detekt-rules-libraries").layout.buildDirectory.file("libs/detekt-rules-libraries-$version.jar"),
        project(":detekt-rules-ruleauthors").layout.buildDirectory.file("libs/detekt-rules-ruleauthors-$version.jar")
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
        fileToUpdate = file("$rootDir/website/src/remark/detektVersionReplace.js")
        linePartToFind = "const detektVersion = "
        lineTransformation = "const detektVersion = \"${Versions.DETEKT}\";"
    }
}

tasks.register("publishToMavenLocal") {
    description = "Publish all the projects to Maven Local"
    subprojects {
        if (this.plugins.hasPlugin("packaging")) {
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
