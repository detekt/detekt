import org.gradle.api.artifacts.ProjectDependency

val ProjectDependency.path: String get() = this.dependencyProject.path
