plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "com.example.printerlibs"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        targetSdk = 34
        aarMetadata {
            minCompileSdk = 29
        }
        consumerProguardFile("lib-proguard-rules.txt")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

publishing {
    publications {
        create<MavenPublication>("printerlibs") {
            groupId = "com.github.nikfaris88"
            artifactId = "printerlibs"
            version = "1.0.0"
            artifact("$buildDir/outputs/aar/printerlibs-release.aar")
        }
    }

    repositories {
        maven {
            name = "com.github.nikfaris88"
            url = uri("${project.buildDir}/testallprinter")
        }
    }

    val generateRepoTask = tasks.register<Zip>("generateRepo") {
        val publishTask = tasks.named(
            "publishReleasePublicationToMyrepoRepository",
            PublishToMavenRepository::class.java
        )
        from(publishTask.map { it.repository.url })
        into("printerlibs")
        archiveFileName.set("printerlibs.zip")
    }

    // Assuming you have a custom task named 'publishToMyRepo' for publishing
    val publishRepoTask = tasks.register("publishRepo") {
        dependsOn("generateRepoTask")
    }
}

dependencies {
    // iMinPrinterSDK dependencies
    implementation(files("libs/imin/iminPrinterSDK.jar"))
    implementation(files("libs/imin/IminLibs1.0.15.jar"))
    implementation(files("libs/wiseasy/SDK4BaseBinderV2.2.12.jar"))
    implementation(files("libs/wiseasy/WiseSdk_P_1.19_00a_23081701.aar"))

    // SunmiPrinter dependencies
    implementation ("com.sunmi:printerx:1.0.15")
}