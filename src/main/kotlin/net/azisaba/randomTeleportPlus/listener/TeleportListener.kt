package net.azisaba.randomTeleportPlus.listener

import net.azisaba.randomTeleportPlus.Permission
import net.azisaba.randomTeleportPlus.RandomTeleportPlus
import net.azisaba.randomTeleportPlus.RandomTeleportPlus.Companion.configuration
import net.azisaba.randomTeleportPlus.manager.MessageManager
import net.azisaba.randomTeleportPlus.manager.sendLangMessage
import net.azisaba.randomTeleportPlus.util.runTaskSync
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.Random

class TeleportListener(private val plugin: RandomTeleportPlus) : Listener {

    @EventHandler
    fun onBlockClick(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock ?: return
        val sign = block.state
        if (sign is Sign) {
            if (!sign.isTeleportSign()) return
            if (!player.hasPermission(Permission.GENERAL)) {
                player.sendLangMessage("no-permission")
                return
            }
            val worldName = sign.getLine(2)
            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                player.sendLangMessage("not-enabled-world")
                return
            }
            if (!configuration.isEnabledWorld(world.name)) {
                player.sendLangMessage("not-enabled-world")
                return
            }
            player.sendLangMessage("teleporting")
            player.randomTeleport(world)
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val sign = event.block.state
        if (sign is Sign) {
            if (!sign.isTeleportSign()) return
            if (!player.hasPermission(Permission.ADMIN)) {
                player.sendLangMessage("no-permission")
                event.isCancelled = true
                return
            }
            player.sendLangMessage("teleporter-deleted")
        }
    }

    @EventHandler
    fun onSign(event: SignChangeEvent) {
        val player = event.player
        if (event.getLine(0).equals("[RandomTP]", ignoreCase = true)) {
            if (!player.hasPermission(Permission.ADMIN)) {
                player.sendLangMessage("no-permission")
                event.isCancelled = true
                return
            }
            val worldName = event.getLine(1) ?: ""
            val world = Bukkit.getWorld(worldName)
            if (world == null) {
                player.sendLangMessage("not-enabled-world")
                event.isCancelled = true
                return
            }
            if (!configuration.isEnabledWorld(world.name)) {
                player.sendLangMessage("not-enabled-world")
                event.isCancelled = true
                return
            }

            event.setLine(0, "§e===============")
            event.setLine(1, MessageManager.coloredPrefix)
            event.setLine(2, world.name)
            event.setLine(3, "§e===============")
            player.sendLangMessage("teleporter-created")
        }
    }

    private fun Sign.isTeleportSign(): Boolean {
        return getLine(1) == MessageManager.coloredPrefix
    }

    fun Player.randomTeleport(world: World) {
        plugin.randomGenerateThread.execute {
            val location = world.getRandomLocation()
            if (location == null) {
                sendLangMessage("safe-location-not-found")
                return@execute
            }
            sendLangMessage("safe-location-found")
            plugin.runTaskSync {
                if (configuration.forceSafety) {
                    location.block.type = Material.STONE
                }
                teleport(location)
            }
        }
    }

    private fun World.getRandomLocation(): Location? {
        val start = System.currentTimeMillis()
        var location = generateRandomLocation(this)
        if (configuration.forceSafety) return location
        while (!location.isSafetyLocation()) {
            if (start + 3000 < System.currentTimeMillis()) {
                return null
            }
            location = generateRandomLocation(this)
        }
        return location
    }

    private fun Location.isSafetyLocation(): Boolean {
        fun Block.isUnSafety() = type.isOccluding || isLiquid

        if (block.isUnSafety()) return false
        if (block.getRelative(0, 1, 0).isUnSafety()) return false
        if (block.getRelative(0, 2, 0).isUnSafety()) return false
        if (!block.getRelative(0, -1, 0).type.isOccluding) return false
        return true
    }

    private fun generateRandomLocation(world: World): Location {
        val size = world.worldBorder.size
        val rand = Random()
        val x = rand.nextInt(size.toInt()) - (size.toInt() / 2)
        val z = rand.nextInt(size.toInt()) - (size.toInt() / 2)
        val y = world.getHighestBlockYAt(x, z) + 1
        return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
    }
}