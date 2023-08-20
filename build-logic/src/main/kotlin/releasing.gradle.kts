import com.vdurmont.semver4j.Semver

plugins {
    id("com.github.breadmoirai.github-release")
    id("io.github.gradle-nexus.publish-plugin")
}

nexusPublishing {
    packageGroup = "io.gitlab.arturbosch"

    repositories {
        create("sonatype") {
            username = providers.environmentVariable("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")
            password = providers.environmentVariable("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")
        }
    }
}

val releaseArtifacts: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
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
    releaseAssets.setFrom(releaseArtifacts)
}

dependencies {
    releaseArtifacts(project(":detekt-cli")) {
        targetConfiguration = "shadow" // com.github.jengelman.gradle.plugins.shadow.ShadowBasePlugin.CONFIGURATION_NAME
    }
    releaseArtifacts(project(":detekt-cli")) {
        targetConfiguration = "shadowDist"
    }
    releaseArtifacts(project(":detekt-generator")) {
        targetConfiguration = "shadow" // com.github.jengelman.gradle.plugins.shadow.ShadowBasePlugin.CONFIGURATION_NAME
    }
    releaseArtifacts(project(":detekt-formatting")) {
        targetConfiguration = Dependency.DEFAULT_CONFIGURATION
        isTransitive = false
    }
    releaseArtifacts(project(":detekt-rules-libraries")) {
        targetConfiguration = Dependency.DEFAULT_CONFIGURATION
        isTransitive = false
    }
    releaseArtifacts(project(":detekt-rules-ruleauthors")) {
        targetConfiguration = Dependency.DEFAULT_CONFIGURATION
        isTransitive = false
    }
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
}

tasks.register("publishAllToSonatypeSnapshot") {
    description = "Publish all the projects to Sonatype Snapshot Repository"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishAllPublicationsToSonatypeSnapshotRepository"))
        }
    }
}

tasks.register("publishAllToMavenCentral") {
    description = "Publish all the projects to Sonatype Staging Repository"
    subprojects {
        if (this.plugins.hasPlugin("publishing")) {
            dependsOn(tasks.named("publishAllPublicationsToMavenCentralRepository"))
        }
    }
}
