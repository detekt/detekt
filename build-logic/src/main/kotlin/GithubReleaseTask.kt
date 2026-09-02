import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import org.kohsuke.github.GitHub
import java.io.File

@DisableCachingByDefault(because = "Creates GitHub releases through an external API")
abstract class GithubReleaseTask : DefaultTask() {

    @get:Internal
    abstract val token: Property<String>

    @get:Input
    abstract val owner: Property<String>

    @get:Input
    abstract val repositoryName: Property<String>

    @get:Input
    abstract val tagName: Property<String>

    @get:Input
    abstract val releaseName: Property<String>

    @get:Input
    abstract val body: Property<String>

    @get:Input
    abstract val targetCommitish: Property<String>

    @get:Input
    abstract val prerelease: Property<Boolean>

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val releaseAssets: ConfigurableFileCollection

    @TaskAction
    fun release() {
        val githubToken = token.get()
        require(githubToken.isNotBlank()) { "GitHub token must not be blank" }

        val repositoryOwner = owner.get()
        require(repositoryOwner.isNotBlank()) { "GitHub repository owner must not be blank" }
        val repository = repositoryName.get()
        require(repository.isNotBlank()) { "GitHub repository name must not be blank" }
        val releaseTag = tagName.get()
        require(releaseTag.isNotBlank()) { "GitHub release tag must not be blank" }
        val releaseTitle = releaseName.get()
        require(releaseTitle.isNotBlank()) { "GitHub release name must not be blank" }
        val releaseBody = body.get()
        val targetRef = targetCommitish.get()
        require(targetRef.isNotBlank()) { "GitHub target commitish must not be blank" }
        val isPrerelease = prerelease.get()

        val assetFiles = releaseAssets.files.sortedBy { it.name }
        require(assetFiles.isNotEmpty()) { "At least one release asset is required" }
        val duplicateAssetsByName = assetFiles
            .groupBy { it.name }
            .filterValues { files -> files.size > 1 }
            .toSortedMap()
        require(duplicateAssetsByName.isEmpty()) {
            duplicateAssetsByName.entries.joinToString(
                prefix = "Release asset names must be unique; duplicates: ",
                separator = "; ",
            ) { (name, files) -> "$name (${files.joinToString { it.absolutePath }})" }
        }
        val assets = assetFiles.map { asset ->
            require(asset.isFile) { "Release asset does not exist: $asset" }
            require(asset.length() > 0) { "Release asset is empty: $asset" }
            asset to asset.contentType()
        }

        val github = GitHub.connectUsingOAuth(githubToken)
        val githubRepository = github.getRepository("$repositoryOwner/$repository")
        githubRepository.getReleaseByTagName(releaseTag)?.delete()

        val release = githubRepository.createRelease(releaseTag)
            .name(releaseTitle)
            .body(releaseBody)
            .commitish(targetRef)
            .prerelease(isPrerelease)
            .draft(true)
            .create()

        assets.forEach { (asset, contentType) -> release.uploadAsset(asset, contentType) }
        release.update().draft(false).update()
    }

    private fun File.contentType(): String =
        when (extension.lowercase()) {
            "jar" -> "application/java-archive"
            "zip" -> "application/zip"
            else -> error("Unsupported release asset type: $name")
        }
}
