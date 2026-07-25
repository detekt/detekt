import org.semver4j.Semver

plugins {
    id("com.github.breadmoirai.github-release")
}

val releaseArtifacts = configurations.dependencyScope("releaseArtifacts")
val releaseAssetFiles = configurations.resolvable("releaseAssetFiles") {
    extendsFrom(releaseArtifacts)
}

val version = Versions.currentOrSnapshot()

githubRelease {
    token(providers.gradleProperty("github.token"))
    owner = "detekt"
    repo = "detekt"
    overwrite = true
    dryRun = false
    prerelease = true
    targetCommitish = "main"
    body = "Detekt Release Body"
    releaseAssets.setFrom(releaseAssetFiles)
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
    releaseArtifacts(project(":detekt-compiler-plugin")) {
        targetConfiguration = "shadow" // com.github.jengelman.gradle.plugins.shadow.ShadowBasePlugin.CONFIGURATION_NAME
    }
    releaseArtifacts(project(":detekt-rules-ktlint-wrapper")) {
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
    register("incrementPatch") {
        notCompatibleWithConfigurationCache("cannot serialize Gradle script object references")
        doLast { updateVersion { it.nextPatch() } }
    }
    register("incrementMinor") {
        notCompatibleWithConfigurationCache("cannot serialize Gradle script object references")
        doLast { updateVersion { it.nextMinor() } }
    }
    register("incrementMajor") {
        notCompatibleWithConfigurationCache("cannot serialize Gradle script object references")
        doLast { updateVersion { it.nextMajor() } }
    }

    register<UpdateVersionInFileTask>("applyDocVersion") {
        fileToUpdate = file("$rootDir/website/src/remark/detektVersionReplace.js")
        linePartToFind = "const detektVersion = "
        lineTransformation = "const detektVersion = \"${Versions.DETEKT}\";"
    }
}

tasks.register("publishToMavenLocal") {
    description = "Publish included builds to Maven Local"
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishToMavenLocal"))
}

tasks.register("publishToMavenCentral") {
    description = "Publish included builds to Maven Central"
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishToMavenCentral"))
}

tasks.register("publishAndReleaseToMavenCentral") {
    description = "Publish and release included builds to Maven Central"
    dependsOn(gradle.includedBuild("detekt-gradle-plugin").task(":publishAndReleaseToMavenCentral"))
}
