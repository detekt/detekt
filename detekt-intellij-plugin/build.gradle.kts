import org.jetbrains.intellij.IntelliJPluginExtension
import org.jetbrains.intellij.tasks.RunIdeaTask

repositories {
    maven {
        setUrl("http://dl.bintray.com/jetbrains/intellij-plugin-service")
    }
}

apply {
    plugin("java")
    plugin("kotlin")
}

plugins {
    id("org.jetbrains.intellij") version "0.2.17"
}

configurations {
    get("implementation").extendsFrom(get("kotlinImplementation"))
    get("testImplementation").extendsFrom(get("kotlinTest"))
}

dependencies {
    compile(project(":detekt-core"))
    compile(project(":detekt-rules"))
}

configure<IntelliJPluginExtension> {
    pluginName = "Detekt IntelliJ Plugin"
    version = "2017.3.4"
    updateSinceUntilBuild = false
    setPlugins("IntelliLang", "Kotlin")
}

tasks.withType<RunIdeaTask> {
    systemProperty(
            "idea.ProcessCanceledException",
            "disabled"
    )
}
