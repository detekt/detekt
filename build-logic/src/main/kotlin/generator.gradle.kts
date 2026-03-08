import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import kotlin.io.path.createDirectories

project.plugins.withId("org.jetbrains.kotlin.jvm") {
    val detektGenerator by configurations.dependencyScope("detektGenerator")
    val detektGeneratorClasspath by configurations.resolvable("detektGeneratorClasspath") {
        extendsFrom(detektGenerator)
    }

    dependencies {
        detektGenerator(project(":detekt-generator"))
    }

    val kotlinExtension = project.extensions.getByType<KotlinProjectExtension>()
    val mainSourceSet = kotlinExtension.sourceSets.getByName("main")
    val kotlinDirs = mainSourceSet.kotlin.sourceDirectories
    val generatedDetektDir = project.layout.buildDirectory.map { it.dir("generated").dir("detekt").asFile }
    val configDir = generatedDetektDir.map { it.resolve("config") }
    val documentationDir = generatedDetektDir.map { it.resolve("documentation") }

    val generateConfig = tasks.register<JavaExec>("generateConfig") {
        inputs.files(kotlinDirs)
        outputs.dir(generatedDetektDir)

        classpath = detektGeneratorClasspath
        mainClass = "dev.detekt.generator.Main"

        doFirst {
            generatedDetektDir.get().deleteRecursively()
            configDir.get().toPath().createDirectories()
            documentationDir.get().toPath().createDirectories()
        }

        argumentProviders.add {
            listOf(
                "--input",
                kotlinDirs.filter { it.exists() }.joinToString(","),
                "--documentation",
                documentationDir.get().toString(),
                "--config",
                configDir.get().toString(),
            )
        }
    }

    val copyConfigToResources by tasks.registering(Copy::class) {
        from(generateConfig)
        into(mainSourceSet.resources.srcDirs.single().resolve("config"))
        include("config.yml")
    }

    val generatedConfig by configurations.consumable("generatedConfig")
    val generatedDeprecations by configurations.consumable("generatedDeprecations")
    val generatedDocumentation by configurations.consumable("generatedDocumentation")

    artifacts {
        add(generatedConfig.name, configDir.map { it.resolve("config.yml") }) {
            builtBy(generateConfig)
        }
        add(generatedDeprecations.name, configDir.map { it.resolve("deprecation.properties") }) {
            builtBy(generateConfig)
        }
        add(generatedDocumentation.name, documentationDir) {
            builtBy(generateConfig)
        }
    }

    val extension = project.extensions.create<DetektGeneratorExtension>("detektGeneratorConfig").apply {
        addConfigToResources.convention(true)
    }

    if (extension.addConfigToResources.get()) {
        tasks.named("processResources").configure {
            inputs.files(copyConfigToResources)
        }
        tasks.named("sourcesJar").configure {
            inputs.files(copyConfigToResources)
        }
    }
}

interface DetektGeneratorExtension {
    val addConfigToResources: Property<Boolean>
}
