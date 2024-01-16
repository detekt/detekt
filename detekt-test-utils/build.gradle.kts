plugins {
    id("module")
    id("public-api")
}

dependencies {
    api(libs.kotlin.stdlibJdk8)
    api(libs.junit.api)
    implementation(projects.detektParser)
    implementation(libs.kotlin.mainKts)
    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.assertj)
}

apiValidation {
    ignoredPackages.add("io.github.detekt.test.utils.internal")
}
