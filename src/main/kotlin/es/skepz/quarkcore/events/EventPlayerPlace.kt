package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockPlaceEvent

class EventPlayerPlace(val core: QuarkCore) : CoreEvent(core) {

    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        val player = event.player

        // check if prison world
        if (player.world.name != core.config.getString("prison-world")) {
            return
        }

        // check if player is in a mine
        if (core.files.config.cfg.getBoolean("can-place-in-mines")) {
            for (mine in core.mines) {
                if (mine.containsBlock(event.block.location) && mine.canBreak(player)) {
                    return
                }
            }
        }

        // check if player has permission to place blocks
        if (!player.hasPermission("quarkcore.place") && !player.hasPermission("quarkcore.edit")
            && !player.hasPermission("*")  && !player.isOp) {
            event.isCancelled = true
            sendMessage(player, "&cYou can't place blocks here!")
        }
    }

}