plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")

    id("com.google.gms.google-services")
}

android {
    namespace = "com.easyrent.abccarapp.abccar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.easyrent.abccarapp.abccar"
        minSdk = 26
        targetSdk = 34
        versionCode = 5
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("QAtest") {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
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

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    val activityVersion = "1.8.0"
    implementation("androidx.activity:activity-ktx:$activityVersion")

    val fragmentVersion = "1.6.1"
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    val navVersion = "2.7.4"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    val dataStoreVer = "1.0.0"
    implementation("androidx.datastore:datastore-preferences:$dataStoreVer")

    // 下拉更新
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Network
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    // EasyPermission
    implementation("com.vmadalin:easypermissions-ktx:1.0.0")

    // Mockk
    testImplementation("io.mockk:mockk:1.13.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    //mixpanel
    implementation("com.mixpanel.android:mixpanel-android:7.+")

}