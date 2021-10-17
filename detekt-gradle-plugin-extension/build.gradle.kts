plugins {
    id("module")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.kotlin.gradlePluginApi)
    compileOnly(gradleKotlinDsl())
}
