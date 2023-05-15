plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

tasks.register<JavaExec>("run") {
    mainClass.set("com.mrboomdev.platformer.DesktopLauncher")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}