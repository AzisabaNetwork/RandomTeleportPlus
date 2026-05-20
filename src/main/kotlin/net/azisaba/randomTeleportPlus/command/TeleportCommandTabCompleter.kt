package net.azisaba.randomTeleportPlus.command

import net.azisaba.randomTeleportPlus.Permission
import net.azisaba.randomTeleportPlus.RandomTeleportPlus
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TeleportCommandTabCompleter(private val plugin: RandomTeleportPlus) : TabCompleter {

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            val completions = mutableListOf<String>()
            completions.add("help")
            if (sender.hasPermission(Permission.ADMIN)) {
                completions.add("reload")
            }
            return completions.filter { it.startsWith(args[0], ignoreCase = true) }.toMutableList()
        }
        return mutableListOf()
    }
}