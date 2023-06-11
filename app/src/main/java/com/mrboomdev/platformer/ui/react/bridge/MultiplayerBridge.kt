package com.mrboomdev.platformer.ui.react.bridge

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class MultiplayerBridge(context: ReactApplicationContext) : ReactContextBaseJavaModule(context) {

    override fun getName(): String {
        return "MultiplayerBridge"
    }

    @ReactMethod
    fun joinRoom(params: ReadableMap, promise: Promise) {
        promise.reject("Unsupported feature", "Multiplayer isn't implemented yet!")
    }

    @ReactMethod
    fun createRoom(params: ReadableMap, promise: Promise) {
        promise.reject("Unsupported feature", "Multiplayer isn't implemented yet!")
    }

    @ReactMethod
    fun leaveRoom(params: ReadableMap, promise: Promise) {
        promise.reject("Unsupported feature", "Multiplayer isn't implemented yet!")
    }

    @ReactMethod
    fun play(params: ReadableMap, promise: Promise) {
        promise.reject("Unsupported feature", "Multiplayer isn't implemented yet!")
    }
}