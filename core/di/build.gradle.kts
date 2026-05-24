plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.emdp.rickandmorty.core.di"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.core.ui)
    implementation(projects.features.home)
    implementation(projects.features.advancedsearch)
    implementation(projects.features.characterslist)
    implementation(projects.features.characterdetail)

    implementation(libs.koin.android)
}