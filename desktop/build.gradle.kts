plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

tasks.register<JavaExec>("run") {
    mainClass.set("com.mrboomdev.platformer.DesktopLauncher")
}

sourceSets {
    main {
        java.srcDir("../assets")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}