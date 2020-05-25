dependencies {
    implementation(kotlin("compiler-embeddable"))
    implementation(project(":detekt-psi-utils"))
    implementation("org.jgrapht:jgrapht-core:${Versions.JGRAPHT}")
    implementation("org.jgrapht:jgrapht-demo:${Versions.JGRAPHT}")

    testImplementation(project(":detekt-test-utils"))
    testImplementation("io.strikt:strikt-core:0.26.1")
}
