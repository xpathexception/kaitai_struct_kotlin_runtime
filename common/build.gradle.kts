plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.kapt)
}

kotlin {
    jvm()
    iosSimulatorArm64()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.okio.core)
            implementation(libs.okio.fakefs)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

task("testClasses")
