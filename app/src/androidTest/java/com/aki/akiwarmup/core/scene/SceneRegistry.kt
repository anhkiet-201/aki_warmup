package com.aki.akiwarmup.core.scene

import com.aki.akiwarmup.core.dsl.Scene

object SceneRegistry {
    private val scenes = mutableMapOf<String, Scene>()
    
    fun register(scene: Scene) {
        scenes[scene.name] = scene
    }
    
    fun get(name: String): Scene {
        return scenes[name] ?: throw IllegalArgumentException("Scene '$name' not found. Registered: ${scenes.keys}")
    }
}
