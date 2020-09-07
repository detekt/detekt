import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
    `java-test-fixtures`
}

dependencies {
    implementation("org.yaml:snakeyaml")
    api(kotlin("compiler-embeddable"))
    api(project(":detekt-psi-utils"))

    testImplementation(project(":detekt-parser"))
    testImplementation(project(":detekt-test"))

    testFixturesApi(kotlin("stdlib-jdk8"))

    dokkaJekyllPlugin("org.jetbrains.dokka:jekyll-plugin")
}

val javaComponent = components["java"] as AdhocComponentWithVariants
listOf(configurations.testFixturesApiElements, configurations.testFixturesRuntimeElements).forEach { config ->
    config.configure {
        javaComponent.withVariantsFromConfiguration(this) {
            skip()
        }
    }
}

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(rootDir.resolve("docs/pages/kdoc"))
}
