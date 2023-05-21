plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    application
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        resources.srcDir("../../assets")
    }
}

dependencies {
    implementation(project(":game"))
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-platform:1.11.0:natives-desktop")
}

application {
    mainClass.set("com.mrboomdev.platformer.DesktopLauncher")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}