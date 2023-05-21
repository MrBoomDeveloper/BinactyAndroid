package com.mrboomdev.platformer

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.mrboomdev.platformer.game.GameManager

class DesktopLauncher {
    init {
        val config = LwjglApplicationConfiguration()
        config.title = "Binacty Engine"
        config.width = 800
        config.height = 400
        LwjglApplication(GameManager(), config)
    }
}

fun main() {
    System.setProperty("user.name", "User")
    System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true")
    DesktopLauncher()
}