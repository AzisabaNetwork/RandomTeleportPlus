package net.azisaba.randomTeleportPlus

import net.azisaba.randomTeleportPlus.command.TeleportCommand
import net.azisaba.randomTeleportPlus.command.TeleportCommandTabCompleter
import net.azisaba.randomTeleportPlus.listener.TeleportListener
import net.azisaba.randomTeleportPlus.manager.ConfigManager
import net.azisaba.randomTeleportPlus.manager.MessageManager
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RandomTeleportPlus : JavaPlugin() {

    companion object {
        lateinit var instance: RandomTeleportPlus
        lateinit var configuration: ConfigManager
    }

    lateinit var randomGenerateThread: ExecutorService

    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        MessageManager.init(this)
        
        randomGenerateThread = Executors.newFixedThreadPool(10)
        server.pluginManager.registerEvents(TeleportListener(this), this)

        configuration = ConfigManager()
        configuration.loadConfig()
        
        val randomTpCommand = getCommand("randomtp")
        randomTpCommand?.setExecutor(TeleportCommand(this))
        randomTpCommand?.tabCompleter = TeleportCommandTabCompleter(this)
    }

    override fun onDisable() {
        randomGenerateThread.shutdown()
    }
}
