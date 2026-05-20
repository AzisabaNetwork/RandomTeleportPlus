package net.azisaba.randomTeleportPlus.manager

import net.azisaba.randomTeleportPlus.RandomTeleportPlus.Companion.instance
import org.bukkit.ChatColor

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

    fun isEnabledWorld(worldName: String): Boolean {
        return enabledWorlds.contains(worldName)
    }
}