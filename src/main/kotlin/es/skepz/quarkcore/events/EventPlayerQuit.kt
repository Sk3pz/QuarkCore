package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.tuodlib.colorize
import es.skepz.quarkcore.tuodlib.wrappers.CoreEvent
import es.skepz.quarkcore.utils.logout
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

class EventPlayerQuit(val core: QuarkCore) : CoreEvent(core) {

    @EventHandler
    fun onJoin(event: PlayerQuitEvent) {
        event.quitMessage(Component.text(colorize("&8(&c-&8) &7${event.player.name}")))

        logout(core, event.player)
    }

}