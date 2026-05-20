package net.azisaba.randomTeleportPlus.command

import net.azisaba.randomTeleportPlus.Permission
import net.azisaba.randomTeleportPlus.RandomTeleportPlus
import net.azisaba.randomTeleportPlus.RandomTeleportPlus.Companion.configuration
import net.azisaba.randomTeleportPlus.listener.TeleportListener
import net.azisaba.randomTeleportPlus.manager.sendLangMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.Bukkit

class TeleportCommand(private val plugin : RandomTeleportPlus) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (!sender.hasPermission(Permission.GENERAL)) {
            sender.sendLangMessage("no-permission")
            return true
        }

        when (args.getOrNull(0)) {
            null -> {
                val world = sender.world
                if (configuration.isEnabledWorld(world.name)) {
                    sender.sendLangMessage("teleporting")
                    val listener = TeleportListener(plugin)
                    with(listener) {
                        sender.randomTeleport(world)
                    }
                    return true
                } else {
                    sender.sendLangMessage("not-enabled-world")
                    return true
                }
            }
            "help" -> {
                plugin.config.getStringList("help-message").forEach { message ->
                    sender.sendMessage(message.replace("&", "§"))
                }
                return true
            }
            "reload" -> {
                if (!sender.hasPermission(Permission.ADMIN)) {
                    sender.sendLangMessage("no-permission")
                    return true
                }
                plugin.reloadConfig()
                configuration.loadConfig()
                sender.sendLangMessage("config-reload-success")
                return true
            }
            "addworld" -> {
                if (!sender.hasPermission(Permission.ADMIN)) {
                    sender.sendLangMessage("no-permission")
                    return true
                }
                val worldName = args.getOrNull(1)
                if (worldName == null) {
                    sender.sendLangMessage("world-not-specified")
                    return true
                }
                val world = Bukkit.getWorld(worldName)
                if (world == null) {
                    sender.sendLangMessage("world-not-found")
                    return true
                }
                configuration.addWorld(world.name)
                sender.sendLangMessage("world-added", "world" to world.name)
                return true
            }
            "delworld" -> {
                if (!sender.hasPermission(Permission.ADMIN)) {
                    sender.sendLangMessage("no-permission")
                    return true
                }
                val worldName = args.getOrNull(1)
                if (worldName == null) {
                    sender.sendLangMessage("world-not-specified")
                    return true
                }
                configuration.removeWorld(worldName)
                sender.sendLangMessage("world-removed", "world" to worldName)
                return true
            }
        }
        return false
    }
}