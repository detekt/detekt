plugins {
    id("com.github.johnrengelman.shadow")
    module
    application
}

application {
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
}

dependencies {
    implementation(libs.jcommander)
    implementation(projects.detektTooling)
    implementation(projects.detektParser)
    runtimeOnly(projects.detektCore)
    runtimeOnly(projects.detektRules)

    testImplementation(projects.detektTest)
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

tasks.shadowJar {
    mergeServiceFiles()
}

tasks.register<Copy>("moveJarForIntegrationTest") {
    from(tasks.shadowJar)
    into(rootProject.buildDir)
    rename { "detekt-cli-all.jar" }
}
