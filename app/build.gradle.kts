plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // safeargs
    id("androidx.navigation.safeargs.kotlin")
    // hilt
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.androidcourselevel5"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.androidcourselevel5"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // add Glide
    implementation ("com.github.bumptech.glide:glide:4.14.2")

    // NavGraph
    val nav_graph_version = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_graph_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_graph_version")

    // Hilt
    val hilt_version = "2.52"
    implementation("com.google.dagger:hilt-android:$hilt_version")
    kapt("com.google.dagger:hilt-android-compiler:$hilt_version")
    implementation("androidx.fragment:fragment-ktx:1.8.2")

    // Library Retrofit and JSON converter
    val retrofit_version = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit_version")

    // Library OkHttp и OkHttp-interceptor
    val okHttp_version = "4.7.2"
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttp_version")
    implementation("com.squareup.okhttp3:okhttp:$okHttp_version")
}