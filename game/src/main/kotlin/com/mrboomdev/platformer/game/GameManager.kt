package com.mrboomdev.platformer.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.ScreenUtils
import com.mrboomdev.providers.FileUtilProvider

class GameManager: Game() {
    lateinit var batch: SpriteBatch
    companion object {
        lateinit var fileUtilProvider: FileUtilProvider
    }

    override fun render() {
        ScreenUtils.clear(1f, 0f, 0f, 1f)
        batch.begin()
        batch.end()
    }

    override fun create() {
        batch = SpriteBatch()
    }
}