plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id("maven-publish")
}

kotlin {
    jvm()
    iosSimulatorArm64()
    macosArm64()
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlin.stdlib)
            api(libs.okio.core)
            api(libs.okio.fakefs)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

group = "io.kaitai.struct"
version = "0.1.56"

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(javadocJar.get())

            artifactId = "common"
            groupId = project.group.toString()
            version = project.version.toString()

            pom {
                name.set("common")
            }
        }
    }
}

task("testClasses")
