import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Usage:
 *
 *   val moveJarForIntegrationTest by tasks.registering(MoveJarForIntegrationTestTask::class) {
 *       inputs.files(tasks.named("jar"))
 *       outputs.file(rootProject.buildDir.resolve("<target>.jar"))
 *   }
 */
open class MoveJarForIntegrationTestTask : DefaultTask(), Runnable {

    init {
        description = "Copies the jar to the build directory without version so integration tests can find it easier."
        group = "Check"
    }

    @TaskAction
    override fun run() {
        inputs.files.singleFile.copyTo(outputs.files.singleFile, overwrite = true)
    }
}
