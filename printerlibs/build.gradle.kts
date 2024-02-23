plugins {
    id("com.android.library")
    id("maven-publish")
}

android {

    defaultConfig {
        minSdkVersion(21)
        compileSdkVersion(34)
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "com.example.printerlibs"


}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("printerlibs") {
                groupId = "com.github.nikfaris88"
                artifactId = "printerlibs"
                version = "1.0.2"
                artifact("$buildDir/outputs/aar/printerlibs-release.aar")
            }
        }

        repositories {
            maven {
                name = "com.github.nikfaris88"
                url = uri("${project.buildDir}/testallprinter")
            }
        }
    }

}

//tasks.named("checkReleaseManifest") {
//    enabled = false
//}

dependencies {
    // iMinPrinterSDK dependencies
    implementation(files("libs/imin/iminPrinterSDK.jar"))
    implementation(files("libs/imin/IminLibs1.0.15.jar"))
    implementation(fileTree(mapOf("dir" to "libs/wiseasy", "include" to listOf("*.jar", "*.aar"))))
    // SunmiPrinter dependencies
    implementation("com.sunmi:printerx:1.0.15")
}