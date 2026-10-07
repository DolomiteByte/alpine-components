import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    `maven-publish`
}

group = "com.github.DolomiteByte"
version = "0.6.0"

android {
    namespace = "com.dolomitebyte.alpine.components"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures { compose = true }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    api(platform("androidx.compose:compose-bom:2026.06.01"))
    api("androidx.compose.ui:ui")
    implementation("androidx.compose.animation:animation")
    api("androidx.compose.foundation:foundation")
    compileOnly("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    androidTestImplementation(platform("androidx.compose:compose-bom:2026.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.activity:activity-compose:1.12.4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = project.group.toString()
                artifactId = "alpine-components"
                version = project.version.toString()
                pom {
                    name.set("Alpine Components for Android")
                    description.set("DolomiteByte's Alpine design components for Jetpack Compose")
                    url.set("https://github.com/DolomiteByte/alpine-components")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    scm { url.set("https://github.com/DolomiteByte/alpine-components") }
                }
            }
        }
    }
}
