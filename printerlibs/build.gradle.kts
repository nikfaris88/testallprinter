plugins {
    id("com.android.library")
    id("maven-publish")
}

android {

    defaultConfig {
        minSdkVersion(26)
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

//        val generateRepoTask = tasks.register<Zip>("generateRepo") {
//            val publishTask = tasks.named(
//                "publishReleasePublicationToMyrepoRepository",
//                PublishToMavenRepository::class.java
//            )
//            from(publishTask.map { it.repository.url })
//            into("printerlibs")
//            archiveFileName.set("printerlibs.zip")
//        }
//
//        // Assuming you have a custom task named 'publishToMyRepo' for publishing
//        val publishRepoTask = tasks.register("publishRepo") {
//            dependsOn("generateRepoTask")
//        }
    }

}

//tasks.named("checkReleaseManifest") {
//    enabled = false
//}

dependencies {
//    implementation("com.google.android.material:material:1.11.0")

    // iMinPrinterSDK dependencies
    implementation(files("libs/imin/iminPrinterSDK.jar"))
    implementation(files("libs/imin/IminLibs1.0.15.jar"))
    implementation(files("libs/wiseasy/SDK4BaseBinderV2.2.12.jar"))
    implementation(files("libs/wiseasy/WiseSdk.jar"))

    // SunmiPrinter dependencies
    implementation ("com.sunmi:printerx:1.0.15")
}