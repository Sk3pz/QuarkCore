package es.skepz.quarkcore.events

import es.skepz.quarkcore.QuarkCore
import es.skepz.quarkcore.tuodlib.*
import es.skepz.quarkcore.tuodlib.wrappers.CoreEvent
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
            serverBroadcast("${cPri()}Please welcome ${cFoc()}${event.player.name} ${cPri()}to the server!")
            event.player.teleport(getSpawn(quark))
        }

        info(event.player, "Welcome to the server!", "Thanks for playing!", "${cPri()}Welcome to the server!\n" +
                "${cPri()}Be sure to do ${cFoc()}/rules${cPri()} to read the rules!\n" +
                "${cPri()}Do ${cFoc()}/spawn ${cPri()}to go to spawn!\n" +
                "${cPri()}Dont be afraid to ask staff for help!")
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        login(quark, event.player, event)
    }

}