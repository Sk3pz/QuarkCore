package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.skepzlib.*
import es.skepz.quarkcore.skepzlib.wrappers.CoreEvent
import es.skepz.quarkcore.utils.getSpawn
import es.skepz.quarkcore.utils.login
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent

class EventPlayerJoin(private val quark: QuarkCore) : CoreEvent(quark) {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(Component.text(colorize("&8(&a+&8) &7${event.player.name}")))

        if (!event.player.hasPlayedBefore()) {
            serverBroadcast("&7Please welcome &b${event.player.name} &7to the server!")
            event.player.teleport(getSpawn(quark))
        }

        info(event.player, "Welcome to the server!", "Thanks for playing!", "&7Welcome to the server!\n" +
                "&7Be sure to do &3/rules&7 to read the rules!\n" +
                "&7Do &b/spawn &3to go to spawn!\n" +
                "&7Dont be afraid to ask staff for help!")
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        login(quark, event.player, event)
    }

}