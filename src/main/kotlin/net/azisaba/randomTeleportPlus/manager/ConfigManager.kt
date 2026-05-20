package net.azisaba.randomTeleportPlus.manager

import net.azisaba.randomTeleportPlus.RandomTeleportPlus.Companion.instance

class ConfigManager {

    val enabledWorlds = mutableSetOf<String>()
    var forceSafety = false

    init {
        instance.logger.info("loading configuration...")
    }

    fun loadConfig() {
        enabledWorlds.clear()

        val config = instance.config
        forceSafety = config.getBoolean("forceSafety", false)
        
        val worlds = config.getStringList("enable-world")
        enabledWorlds.addAll(worlds)

        instance.logger.info("loaded configuration")
    }

    fun addWorld(worldName: String) {
        enabledWorlds.add(worldName)
        instance.config.set("enable-world", enabledWorlds.toList())
        instance.saveConfig()
    }

    fun removeWorld(worldName: String) {
        enabledWorlds.remove(worldName)
        instance.config.set("enable-world", enabledWorlds.toList())
        instance.saveConfig()
    }

    fun isEnabledWorld(worldName: String): Boolean {
        return enabledWorlds.contains(worldName)
    }
}