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
        }
        return false
    }
}