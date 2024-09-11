

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.psaanalytics.psa_android_tracker"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            buildConfigField ("String", "TRACKER_LABEL", "\"andr-$project.version\"")
            buildConfigField ("String", "TRACKER_VERSION", "\"$project.version\"")
            enableUnitTestCoverage
        }


        release {
            isMinifyEnabled = false
            buildConfigField ("String", "TRACKER_LABEL", "\"andr-$project.version\"")
            buildConfigField ("String", "TRACKER_VERSION", "\"$project.version\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )





        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.android.installreferrer:installreferrer:2.2")
    implementation("androidx.lifecycle:lifecycle-process:2.8.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    implementation ("com.squareup.okhttp3:okhttp:4.11.0")

}