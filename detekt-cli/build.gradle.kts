plugins {
    id("com.github.johnrengelman.shadow")
    module
    application
}

application {
    mainClassName = "io.gitlab.arturbosch.detekt.cli.Main"
}

val bundledRules by configurations.creating

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.beust:jcommander")
    implementation(project(":detekt-tooling"))
    implementation(project(":detekt-parser"))
    runtimeOnly(project(":detekt-core"))

    testImplementation(project(":detekt-test"))

    bundledRules(project(":detekt-rules"))
}

tasks.shadowJar {
    mergeServiceFiles()
    configurations = listOf(project.configurations.runtimeClasspath.get(), bundledRules)
}

val moveJarForIntegrationTest by tasks.registering {
    dependsOn(tasks.named("shadowJar"))
    doLast {
        copy {
            from(tasks.named("shadowJar"))
            into(rootProject.buildDir)
            rename { "detekt-cli-all.jar" }
        }
    }
}
