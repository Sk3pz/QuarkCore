package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.sendMessage
import es.skepz.quarkcore.skepzlib.wrappers.CoreEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class EventPlayerBreak(val core: QuarkCore) : CoreEvent(core) {

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {

        val player = event.player

        // check if prison world
        if (player.world.name != core.config.getString("prison-world")) {
            return
        }

        // check if player is in a mine
        for (mine in core.mines) {
            if (mine.containsBlock(event.block.location) && mine.canBreak(player)) {
                event.isCancelled = true
                mine.breakBlock(player, event.block)
                return
            }
        }

        // check if player has permission to break blocks
        if (!player.hasPermission("quarkcore.break") && !player.hasPermission("quarkcore.edit")
            && !player.hasPermission("*") && !player.isOp) {
            event.isCancelled = true
            sendMessage(player, "&cYou dont have permission to break that!")
        }

    }

}