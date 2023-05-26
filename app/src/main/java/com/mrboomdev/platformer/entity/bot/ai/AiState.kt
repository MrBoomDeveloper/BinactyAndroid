package com.mrboomdev.platformer.entity.bot.ai

import com.mrboomdev.platformer.entity.Entity

class AiState(data: Data) {

    data class Data(val initial: String, val states: Map<Any, Any>)

    data class Transition(val name: String)

    data class State(val transitions: Array<Transition>, val target: Entity.Target)
}