plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xparcelize"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.glide)
    implementation(libs.androidx.lifecycle.viewmodel.ktx.v262)
    implementation(libs.androidx.lifecycle.livedata.ktx.v262)
    implementation(libs.androidx.fragment.ktx.v162)
    implementation(libs.androidx.navigation.fragment.ktx.v274)
    implementation(libs.androidx.navigation.ui.ktx.v274)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.dagger)
    implementation (libs.androidx.media)
    implementation (libs.androidx.core.ktx.v190)
    implementation (libs.androidx.appcompat.v161)
    implementation (libs.androidx.lifecycle.livedata.ktx.v261)
    implementation (libs.androidx.media2.session)
    implementation (libs.androidx.core)
    implementation (libs.androidx.media.v160)
    implementation (libs.androidx.media)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.media3.ui)
    kapt(libs.dagger.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}