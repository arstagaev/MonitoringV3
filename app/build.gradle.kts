plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.tagaev.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tagaev.myapplication"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation (libs.accompanist.permissions)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    // https://mvnrepository.com/artifact/com.tambapps.marcel/marcel-stdlib
    implementation("com.tambapps.fft4j:fft4j:2.0")
    // FFT
    implementation ("com.github.wendykierp:JTransforms:3.1")

    // For Jetpack Compose.
    implementation("com.patrykandpatrick.vico:compose:1.14.0")

    // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
    implementation("com.patrykandpatrick.vico:compose-m3:1.14.0")

    // Houses the core logic for charts and other elements. Included in all other modules.
    implementation("com.patrykandpatrick.vico:core:1.14.0")

//    implementation("co.yml:ycharts:2.1.0")
    //implementation("com.github.tehras:charts:0.2.4-alpha")
    // https://mvnrepository.com/artifact/com.himanshoe/charty
    implementation("com.himanshoe:charty:2.0.0-alpha01")

    // Datetime:
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    // FFT:
    implementation("com.tambapps.fft4j:fft4j:2.0")
    implementation ("com.github.wendykierp:JTransforms:3.1")


    implementation(project(path = ":flowble"))
}